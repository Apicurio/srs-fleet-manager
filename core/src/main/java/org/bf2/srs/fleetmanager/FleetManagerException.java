package org.bf2.srs.fleetmanager;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class FleetManagerException extends Exception implements UserError {

    public FleetManagerException() {
    }

    public FleetManagerException(String message) {
        super(message);
    }

    public FleetManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        var message = super.getMessage();
        return message != null ? message : getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_UNKNOWN);
    }
}
