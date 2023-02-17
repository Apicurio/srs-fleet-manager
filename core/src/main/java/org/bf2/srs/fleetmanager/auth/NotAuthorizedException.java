package org.bf2.srs.fleetmanager.auth;

import org.bf2.srs.fleetmanager.FleetManagerException;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class NotAuthorizedException extends FleetManagerException {

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_NOT_AUTHORIZED);
    }
}
