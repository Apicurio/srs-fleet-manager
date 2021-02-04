package io.bf2fc6cc711aee1a0c2a.execution.manager;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface WorkerContext {

    /**
     * Delay execution of a piece of code after the main {@link Worker#execute(Task, WorkerContext)} method
     * has finished (without exceptions).
     * This may be particularly useful when submitting child tasks that need to be started
     * after the transaction on the main method finishes.
     */
    void delay(Runnable action);

    /**
     * Stop the execution of the current task immediately and retry later if possible.
     */
    void retry();

    /**
     * Stop the execution of the current task immediately and retry later if possible.
     *
     * @param minRetries Provide a minimal number of retries to attempt (in total). Unless the {@link #forceRetry()} is used,
     *                   this will likely also be the total number of retries.
     */
    void retry(int minRetries);

    /**
     * Stop the execution of the current task immediately and retry as soon as possible.
     * When using this method, the task is guaranteed to be retried at least one more time,
     * under normal operation.
     */
    void forceRetry();

    /**
     * Stop the execution of the current task immediately and don't schedule it anymore.
     * {@link Worker#finallyExecute(Task, WorkerContext, java.util.Optional)}
     * will be executed.
     */
    void stop();
}
