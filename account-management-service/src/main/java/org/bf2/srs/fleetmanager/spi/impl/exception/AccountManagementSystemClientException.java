package org.bf2.srs.fleetmanager.spi.impl.exception;

import io.apicurio.rest.client.error.ApicurioRestClientException;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Error;

import java.util.Optional;

public class AccountManagementSystemClientException extends ApicurioRestClientException implements UserError {

    private static final long serialVersionUID = 1L;

    private final Optional<Error> cause;

    public AccountManagementSystemClientException(Error cause) {
        super(String.format("Error found when executing action with kind: %s, code: %s, href: %s, id: %s, operationId: %s, and reason: %s",
                cause.getKind(), cause.getCode(), cause.getHref(), cause.getId(), cause.getOperationId(), cause.getReason()));
        this.cause = Optional.of(cause);
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
        this.cause = Optional.empty();
    }

    public AccountManagementSystemClientException(Throwable error) {
        super(error.getMessage());
        this.cause = Optional.empty();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        var reason = cause.isPresent() ? ". " + cause.get().getReason() : ".";
        return UserErrorInfo.create(UserErrorCode.ERROR_AMS_FAILED_TO_CHECK_QUOTA, reason);
    }
}
