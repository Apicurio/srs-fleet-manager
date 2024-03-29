package org.bf2.srs.fleetmanager.execution.manager.impl;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@AllArgsConstructor(access = PRIVATE)
@Getter
@ToString
public class RetryExecutionControlException extends ExecutionControlException {

    private static final long serialVersionUID = -8571538574408626778L;

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
