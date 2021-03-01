package org.bf2.srs.fleetmanager.execution.manager.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@NoArgsConstructor
@ToString
public class WorkerContextImpl implements WorkerContext {

    @Getter
    @Setter
    @JsonIgnore
    private List<Runnable> delayedActions = new ArrayList<>();

    // Current number of attempts
    @Getter
    @Setter
    private int retryAttempts;

    // Current # of max retries. Applies if > 0
    @Getter
    @Setter
    private int minRetries;

    @Builder
    private WorkerContextImpl(int minRetries) {
        this.minRetries = minRetries;
    }

    @Override
    public void delay(Runnable delayedAction) {
        delayedActions.add(delayedAction);
    }

    @Override
    public void retry() {
        throw RetryExecutionControlException.create(0, false);
    }

    @Override
    public void retry(int minRetries) {
        if (minRetries < 1) {
            throw new IllegalArgumentException();
        }
        throw RetryExecutionControlException.create(minRetries, false);
    }

    @Override
    public void forceRetry() {
        throw RetryExecutionControlException.create(0, true);
    }

    @Override
    public void stop() {
        throw StopExecutionControlException.create();
    }
}
