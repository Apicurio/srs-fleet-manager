package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

public class ResourceLimitReachedException extends Exception implements UserError {

    public ResourceLimitReachedException() {
        super();
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_AMS_RESOURCE_LIMIT_REACHED);
    }
}
