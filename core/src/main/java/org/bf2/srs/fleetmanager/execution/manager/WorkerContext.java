package org.bf2.srs.fleetmanager.execution.manager;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public interface WorkerContext {

    /**
     * Delay execution of a piece of code after the main {@link Worker#execute(Task, WorkerContext)} method
     * OR the finalizer has finished. This is skipped when an exception is thrown, INCLUDING retry/stop commands.
     * <p>
     * This may be particularly useful when submitting child tasks that need to be started
     * after the transaction on the main method finishes.
     */
    void delay(Runnable action);

    /**
     * Stop the execution of the current task immediately and retry later if possible.
     * Using this command disregards the normal schedule.
     * If you have a periodic task it may be useful to just wait on the next scheduled execution.
     * <p>
     * WARNING: If you are executing in a transaction, this method will abort it.
     * Use `ctx.delay(ctx::retry);` instead if this behavior is not desirable.
     */
    void retry();

    /**
     * @param minRetries Provide a minimal number of retries to attempt (in total). Unless the {@link #forceRetry()} is used,
     *                   this will likely also be the total number of retries.
     * @see WorkerContext#retry()
     */
    void retry(int minRetries);

    /**
     * When using this method, the task is guaranteed to be retried at least one more time,
     * under normal operation.
     *
     * @see WorkerContext#retry()
     */
    void forceRetry();

    /**
     * Stop the execution of the current task immediately and don't schedule it anymore.
     * {@link Worker#finallyExecute(Task, WorkerContext, java.util.Optional)}
     * will still be executed.
     * <p>
     * WARNING: If you are executing in a transaction, this method will abort it.
     * Use `ctx.delay(ctx::stop);` instead if this behavior is not desirable.
     */
    void stop();
}
