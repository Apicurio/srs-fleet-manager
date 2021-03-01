package org.bf2.srs.fleetmanager.execution.manager;

import java.util.Optional;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface Worker {

    /**
     * Get an unique identifier for this implementation of `Worker`.
     * <p>
     * Instances of the same worker implementation MUST have the same type.
     */
    String getType();

    /**
     * Given a task, return `true` if this worker is supposed to process the task.
     * This method must run as fast as possible.
     */
    boolean supports(Task aTask);

    /**
     * Execute this worker on the provided supported task.
     * The execution is retried (if possible) when an exception is thrown.
     */
    void execute(Task aTask, WorkerContext ctl) throws Exception;

    /**
     * This method MUST be always executed a single time, just before the task is permanently unscheduled.
     * i.e. If there are any retries scheduled, this method is not called.
     * <p>
     * It is executed whether there was an error or not. In the latter case,
     * the last known error is provided as an optional argument.
     * This error argument is empty if the execution was caused by an explicit retry or stop using
     * (@link {@link WorkerContext}.
     * <p>
     * This also means that when the application is shutting down,
     * the method will be NOT be usually executed, since the task is assumed to be persisted to a database. only for tasks that are NOT persistent.
     * <p>
     * IMPORTANT: This method is not `default` to force implementors to handle failure states.
     **/
    void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws Exception;
}
