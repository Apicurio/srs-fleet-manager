package io.bf2fc6cc711aee1a0c2a.execution.manager.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@AllArgsConstructor(access = PRIVATE)
@Getter
@ToString
public class RetryExecutionControlException extends ExecutionControlException {

    private final int minRetries;

    private final boolean force;

    /**
     * @param minRetries Request a minimum number of retries. MUST NOT be negative. Ignored if 0.
     * @param force      Force a retry ASAP, ignore total retries and backoff delay.
     */
    static RetryExecutionControlException create(int minRetries, boolean force) {
        if (minRetries < 0)
            throw new IllegalArgumentException("Argument `minRetries` must not be negative.");

        return new RetryExecutionControlException(minRetries, force);
    }
}
