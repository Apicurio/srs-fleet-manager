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
        if (cause.isPresent()) {
            switch (cause.get().getCode()) {
                case "ACCT-MGMT-7":
                    return UserErrorInfo.create(UserErrorCode.ERROR_AMS_ACCOUNT_NOT_FOUND, cause.get().getReason());
                default:
                    break; // Bubble down to UserErrorCode.ERROR_UNKNOWN
            }
        }
        return UserErrorInfo.create(UserErrorCode.ERROR_UNKNOWN);
    }
}
