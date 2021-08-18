package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

import static java.util.Objects.requireNonNull;

public class ErrorNotFoundException extends Exception implements UserError {

    private final String id;

    public ErrorNotFoundException(String id) {
        requireNonNull(id);
        this.id = id;
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_NOT_FOUND_ERROR_TYPE, id);
    }
}
