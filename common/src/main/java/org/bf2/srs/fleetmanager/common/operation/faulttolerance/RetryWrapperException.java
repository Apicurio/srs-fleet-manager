package org.bf2.srs.fleetmanager.common.operation.faulttolerance;

import lombok.Getter;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class RetryWrapperException extends RuntimeException {

    @Getter
    private Exception wrapped;

    public RetryWrapperException(Exception cause) {
        super(cause);
        this.wrapped = cause;
    }
}
