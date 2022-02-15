package org.bf2.srs.fleetmanager.common.operation.faulttolerance;

import lombok.Getter;

public class RetryWrapperException extends RuntimeException {

    @Getter
    private RuntimeException wrapped;

    public RetryWrapperException(RuntimeException cause) {
        super(cause);
        this.wrapped = cause;
    }
}
