package org.bf2.srs.fleetmanager.spi;

import lombok.Getter;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.bf2.srs.fleetmanager.spi.model.AMSError;

import java.util.Optional;

public class AccountManagementServiceClientException extends RuntimeException implements UserError {

    private static final long serialVersionUID = -8929778890921534692L;

    @Getter
    private final Optional<AMSError> causeEntity;

    @Getter
    private final Optional<Integer> statusCode;

    public AccountManagementServiceClientException(Optional<AMSError> causeEntity, Optional<Integer> statusCode, Exception cause) {
        super(cause.getMessage(), cause);
        this.causeEntity = causeEntity;
        this.statusCode = statusCode;
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        // TODO Do we want to expose underlying error reason to the users?
        var reason = causeEntity.map(error -> ". " + error.getReason()).orElse(".");
        return UserErrorInfo.create(UserErrorCode.ERROR_AMS_FAILED_TO_CHECK_QUOTA, reason);
    }
}
