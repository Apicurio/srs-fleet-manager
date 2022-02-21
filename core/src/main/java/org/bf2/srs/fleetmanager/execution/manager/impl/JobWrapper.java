package org.bf2.srs.fleetmanager.execution.manager.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.bf2.srs.fleetmanager.common.SerDesObjectMapperProducer;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.Worker;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.execution.manager.impl.QuartzIDs.jobDetailKeyForTask;
import static org.bf2.srs.fleetmanager.execution.manager.impl.QuartzIDs.jobDetailKeyForWorker;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobWrapper implements Job {

    private static final long MAX_RETRY_DELAY_SEC = 10L * 60L; // 10 minutes

    private final Logger log = LoggerFactory.getLogger(getClass());

    ObjectMapper mapper = SerDesObjectMapperProducer.getMapper();

    @Inject
    Instance<Worker> workers;

    @Inject
    QuartzTaskManager taskManager;

    @Inject
    OperationContext opCtx;

    private Set<Class<? extends Worker>> workerExclusions = ConcurrentHashMap.newKeySet();

    @Override
    @SneakyThrows
    @ActivateRequestContext
    public void execute(JobExecutionContext quartzJobContext) {

        Task task = loadTask(quartzJobContext);

        List<Worker> selectedWorkers = workers.stream()
                .filter(w -> w.supports(task) && !workerExclusions.contains(w.getClass()))
                .collect(toList());

        for (Worker worker : selectedWorkers) {

            WorkerContextImpl wCtx = loadWorkerContext(quartzJobContext, worker, task);

            Instant next = null;
            Exception lastException = null;

            try {
                log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Executing task.",
                        task, worker, wCtx);

                worker.execute(task, wCtx);
                wCtx.getDelayedActions().forEach(Runnable::run);
                // OK vvv
                wCtx.setRetryAttempts(0); // Reset retry counter
                wCtx.setMinRetries(task.getSchedule().getMinRetries()); // Reset min retry counter

                next = nextExecution(task); // Normal rescheduling

            } catch (Exception anEx) { // TODO Throwable?

                lastException = anEx;

                if (anEx instanceof RetryExecutionControlException) {
                    log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Task requested a retry.",
                            task, worker, wCtx, anEx);

                    RetryExecutionControlException ex = (RetryExecutionControlException) anEx;

                    if (ex.isForce() && wCtx.getMinRetries() < Integer.MAX_VALUE) {
                        // Make space for forced retry, no more than Integer.MAX_VALUE
                        wCtx.setMinRetries(wCtx.getMinRetries() + 1);
                        next = Instant.now().plus(Duration.ofSeconds(1));
                    }
                    if (ex.getMinRetries() > wCtx.getMinRetries()) {
                        wCtx.setMinRetries(ex.getMinRetries());
                    }
                    lastException = null;
                }

                if (wCtx.getRetryAttempts() < wCtx.getMinRetries() && (next == null)) {
                    // Reschedule if the minRetries is not reached
                    next = Instant.now().plus(backoff(wCtx.getRetryAttempts()));
                }

                if (anEx instanceof StopExecutionControlException) {
                    log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Task requested a stop.",
                            task, worker, wCtx, anEx);
                    // Unschedule
                    next = null;
                    lastException = null;
                }

                if (lastException != null) {
                    log.warn("Task Manager (task = {}, worker = {}, workerContext = {}, nextExecution = {}): Task threw an exception during execution: {}",
                            task, worker, wCtx, next, anEx);
                }

                wCtx.setRetryAttempts(wCtx.getRetryAttempts() + 1);

            } finally {

                wCtx.setDelayedActions(new ArrayList<>(0)); // Unlikely used
                saveWorkerContext(quartzJobContext, wCtx, worker);
                saveTask(quartzJobContext, task);

                // Scheduling
                if (next != null) {
                    if (wCtx.getRetryAttempts() == wCtx.getMinRetries()) {
                        log.info("Task Manager (task = {}, worker = {}, workerContext = {}): Last rescheduling at {}.",
                                task, worker, wCtx, next);
                    } else {
                        log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Rescheduling task at {}.",
                                task, worker, wCtx, next);
                    }

                    taskManager.rerigger(task, next);
                } else {

                    try {
                        log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Executing finallyExecute. Last exception = {}",
                                task, worker, wCtx, lastException);
                        worker.finallyExecute(task, wCtx, ofNullable(lastException));
                        wCtx.getDelayedActions().forEach(Runnable::run);
                    } catch (Exception ex) {
                        log.warn("Task Manager (task = {}, worker = {}, workerContext = {}): Ignoring an exception thrown in finallyExecute: {}",
                                task, worker, wCtx, ex);
                    } finally {
                        log.debug("Task Manager (task = {}, worker = {}, workerContext = {}): Removing task.",
                                task, worker, wCtx);
                        taskManager.remove(task);
                    }
                }
            }
        }
    }

    @SneakyThrows
    private WorkerContextImpl loadWorkerContext(JobExecutionContext context, Worker worker, Task aTask) {
        String serialized = (String) context.getJobDetail().getJobDataMap().get(jobDetailKeyForWorker(worker));
        return (serialized != null) ?
                mapper.readValue(serialized, WorkerContextImpl.class) :
                WorkerContextImpl.builder().minRetries(aTask.getSchedule().getMinRetries()).build();
    }

    @SneakyThrows
    private void saveWorkerContext(JobExecutionContext context, WorkerContext ctx, Worker worker) {
        String serialized = mapper.writeValueAsString(ctx);
        context.getJobDetail().getJobDataMap().put(jobDetailKeyForWorker(worker), serialized);
    }

    @SneakyThrows
    private Task loadTask(JobExecutionContext context) {
        String serialized = (String) context.getJobDetail().getJobDataMap().get(jobDetailKeyForTask());
        if (serialized == null) {
            throw new IllegalStateException("Task not found in job detail.");
        }
        var task = mapper.readValue(serialized, Task.class);
        // Load Operation Context
        var ctxData = task.getOperationContextData();
        if (opCtx.isContextDataLoaded())
            throw new IllegalStateException("Unexpected state: Operation Context is already loaded");
        if (ctxData == null) {
            log.debug("Creating a new Operation Context. This should only happen when running old tasks, due to upgrades.");
            opCtx.loadNewContextData();
        } else {
            log.debug("Loading existing Operation Context with ID {}.", ctxData.getOperationId());
            opCtx.loadContextData(ctxData);
        }
        return task;
    }

    @SneakyThrows
    private void saveTask(JobExecutionContext context, Task task) {
        // Save Operation Context
        var ctxData = opCtx.getContextData();
        task.setOperationContextData(ctxData);
        String serialized = mapper.writeValueAsString(task);
        context.getJobDetail().getJobDataMap().put(jobDetailKeyForTask(), serialized);
    }

    private static Duration backoff(int retries) {
        if (retries < 0)
            throw new IllegalArgumentException("Argument must be non-negative.");
        if (retries > 20) {
            // Prevent overflow
            return ofSeconds(MAX_RETRY_DELAY_SEC);
        }
        // delay = 2^(retries + 2)
        long delay = 1L << (retries + 2);
        return (delay > MAX_RETRY_DELAY_SEC) ?
                ofSeconds(MAX_RETRY_DELAY_SEC) : ofSeconds(delay);
    }

    private static Instant nextExecution(Task task) {
        var schedule = task.getSchedule();
        requireNonNull(schedule);
        if (schedule.getInterval() != null)
            return now().plus(schedule.getInterval());
        else
            return null; // TODO Optional
    }

    /**
     * Testing support, so we can e.g. replace a specific worker with another.
     */
    public Set<Class<? extends Worker>> getWorkerExclusions() {
        return workerExclusions;
    }
}
