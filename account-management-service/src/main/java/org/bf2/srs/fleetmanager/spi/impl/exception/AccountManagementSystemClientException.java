package org.bf2.srs.fleetmanager.spi.impl.exception;

import io.apicurio.rest.client.error.ApicurioRestClientException;
import lombok.Getter;
import org.bf2.srs.fleetmanager.spi.AccountManagementServiceClientException;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.RetryWrapperException;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Error;
import org.bf2.srs.fleetmanager.spi.model.AMSError;

import java.io.IOException;
import java.util.Optional;

public class AccountManagementSystemClientException extends ApicurioRestClientException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final Optional<Throwable> causeException;

    @Getter
    private final Optional<Error> causeEntity;

    @Getter
    private final Optional<Integer> statusCode;

    public AccountManagementSystemClientException(Error causeEntity, int statusCode) {
        super(String.format("Error '%s' found when executing action. Returned status code is '%s'", causeEntity, statusCode));
        this.causeException = Optional.empty();
        this.causeEntity = Optional.of(causeEntity);
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message, int statusCode) {
        super(message);
        this.causeException = Optional.empty();
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
        this.causeException = Optional.empty();
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.empty();
    }

    public AccountManagementSystemClientException(Throwable error) {
        super(error.getMessage());
        this.causeException = Optional.of(error);
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.empty();
    }

    private boolean shouldRetry() {
        if (statusCode.isPresent()) {
            return statusCode.get() / 100 == 5;
        }
        if(causeException.isPresent()) {
            return causeException.get() instanceof IOException;
        }
        return false;
    }

    public RuntimeException convert() {
        var newCauseEntity = this.causeEntity.map(e -> AMSError.builder()
                .code(e.getCode())
                .reason(e.getReason())
                .build());
        var cex = new AccountManagementServiceClientException(newCauseEntity, this.statusCode, this);
        if(shouldRetry()) {
            return new RetryWrapperException(cex);
        } else {
            return cex;
        }
    }
}
