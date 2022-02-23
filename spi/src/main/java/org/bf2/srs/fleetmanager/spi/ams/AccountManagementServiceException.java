package org.bf2.srs.fleetmanager.spi.ams;

import lombok.Getter;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.bf2.srs.fleetmanager.common.operation.faulttolerance.CanRetry;
import org.bf2.srs.fleetmanager.spi.ams.model.AMSError;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class AccountManagementServiceException extends Exception implements UserError, CanRetry {

    private static final long serialVersionUID = -8929778890921534692L;

    @Getter
    private final Optional<AMSError> causeEntity;

    @Getter
    private final Optional<Integer> statusCode;

    public AccountManagementServiceException(Optional<AMSError> causeEntity, Optional<Integer> statusCode, Exception cause) {
        super(cause.getMessage(), cause);
        this.causeEntity = causeEntity;
        this.statusCode = statusCode;
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        var reason = causeEntity.map(error -> ". " + error.getReason()).orElse(".");
        // TODO This exception can be used more generally than what the error code suggests.
        return UserErrorInfo.create(UserErrorCode.ERROR_AMS_FAILED_TO_CHECK_QUOTA, reason);
    }

    @Override
    public boolean retry() {
        if (statusCode.isPresent()) {
            return statusCode.get() / 100 == 5; // Test if this is the 5xx error code
        }
        if (getCause() instanceof IOException) {
            return true;
        }
        return false;
    }
}
