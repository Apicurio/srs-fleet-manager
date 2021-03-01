package org.bf2.srs.fleetmanager.execution.manager.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static java.util.Date.from;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static org.bf2.srs.fleetmanager.execution.manager.impl.QuartzIDs.*;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class QuartzTaskManager implements TaskManager {

    @Inject
    Scheduler quartzScheduler;

    @Inject
    ObjectMapper mapper;

    @SneakyThrows
    @Override
    public void start() {
        quartzScheduler.start();
    }

    @SneakyThrows
    @Override
    public void submit(Task task) {

        String taskSerialized = mapper.writeValueAsString(task);

        var job = JobBuilder.newJob(JobWrapper.class)
                .withIdentity(jobKeyForTask(task))
                .usingJobData(jobDetailKeyForTask(), taskSerialized)
                .build();

        Instant at = task.getSchedule().getFirstExecuteAt();

        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKeyForTask(task))
                .forJob(jobKeyForTask(task))
                .startAt(from(at))
                .build();

        quartzScheduler.scheduleJob(job, trigger);
    }

    @SneakyThrows
    void rerigger(Task task, Instant at) {

        var trigger = TriggerBuilder.newTrigger()
                .forJob(jobKeyForTask(task))
                .withIdentity(idForTask(task), groupForTask(task))
                .withPriority(task.getSchedule().getPriority())
                .startAt(from(at))
                .build();

        quartzScheduler.rescheduleJob(triggerKeyForTask(task), trigger);
    }

    @SneakyThrows
    @Override
    public Set<Task> getAllTasks() {
        return quartzScheduler.getJobKeys(GroupMatcher.groupStartsWith(TASK_GROUP_PREFIX)).stream()
                .map(this::getJobDetail)
                .map(j -> (String) j.getJobDataMap().get("task"))
                .map(this::deserialize)
                .collect(toSet());
    }

    @SneakyThrows
    private JobDetail getJobDetail(JobKey k) {
        return quartzScheduler.getJobDetail(k);
    }

    @SneakyThrows
    private Task deserialize(String s) {
        return mapper.readValue(s, Task.class);
    }

    @SneakyThrows
    @Override
    public Set<Task> getTasksByType(String taskType) {
        return quartzScheduler.getJobKeys(GroupMatcher.groupEquals(groupForTaskType(taskType))).stream()
                .map(this::getJobDetail)
                .map(j -> (String) j.getJobDataMap().get(jobDetailKeyForTask()))
                .map(this::deserialize)
                .collect(toSet());
    }

    @Override
    public Optional<Task> getTaskById(String taskId) {
        requireNonNull(taskId);
        return getAllTasks().stream()
                .filter(t -> taskId.equals(t.getId()))
                .findFirst();
    }

    @SneakyThrows
    @Override
    public void remove(Task task) {
        quartzScheduler.deleteJob(jobKeyForTask(task));
    }

    @SneakyThrows
    @Override
    public void stop() {
        quartzScheduler.shutdown(true);
    }
}
