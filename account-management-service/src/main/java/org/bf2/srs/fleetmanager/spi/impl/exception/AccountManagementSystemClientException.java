package org.bf2.srs.fleetmanager.spi.impl.exception;

import io.apicurio.rest.client.error.ApicurioRestClientException;
import lombok.Getter;
import org.bf2.srs.fleetmanager.spi.AccountManagementServiceClientException;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Error;
import org.bf2.srs.fleetmanager.spi.model.AMSError;

import java.util.Optional;

public class AccountManagementSystemClientException extends ApicurioRestClientException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final Optional<Error> causeEntity;

    @Getter
    private final Optional<Integer> statusCode;

    public AccountManagementSystemClientException(Error causeEntity, int statusCode) {
        super(String.format("Error '%s' found when executing action. Returned status code is '%s'", causeEntity, statusCode));
        this.causeEntity = Optional.of(causeEntity);
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message, int statusCode) {
        super(message);
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.empty();
    }

    public AccountManagementSystemClientException(Throwable error) {
        super(error.getMessage());
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.empty();
    }

    public AccountManagementServiceClientException convert() {
        var newCauseEntity = this.causeEntity.map(e -> AMSError.builder()
                .code(e.getCode())
                .reason(e.getReason())
                .build());
        return new AccountManagementServiceClientException(newCauseEntity, this.statusCode, this);
    }
}
