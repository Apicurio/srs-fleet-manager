package org.bf2.srs.fleetmanager.spi.ams.impl.exception;

import io.apicurio.rest.client.error.ApicurioRestClientException;
import lombok.Getter;
import org.bf2.srs.fleetmanager.spi.ams.impl.model.response.Error;

import java.util.Optional;

public class AccountManagementSystemClientException extends ApicurioRestClientException {

    private static final long serialVersionUID = 1L;

    @Getter
    private Optional<Throwable> causeException = Optional.empty();

    @Getter
    private Optional<Error> causeEntity = Optional.empty();

    @Getter
    private Optional<Integer> statusCode = Optional.empty();

    public AccountManagementSystemClientException(Error causeEntity, int statusCode) {
        super(String.format("Error '%s' found when executing action. Returned status code is '%s'", causeEntity, statusCode));
        this.causeEntity = Optional.of(causeEntity);
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message, int statusCode) {
        super(message);
        this.statusCode = Optional.of(statusCode);
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
    }

    public AccountManagementSystemClientException(Throwable error) {
        super(error.getMessage());
        this.causeException = Optional.of(error);
        this.causeEntity = Optional.empty();
        this.statusCode = Optional.empty();
    }
}
