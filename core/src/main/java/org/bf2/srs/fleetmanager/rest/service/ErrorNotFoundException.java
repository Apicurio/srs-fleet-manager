package org.bf2.srs.fleetmanager.rest.service;

import static java.util.Objects.requireNonNull;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

public class ErrorNotFoundException extends Exception implements UserError {

    private static final long serialVersionUID = 7134512134695788999L;

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
        return UserErrorInfo.create(UserErrorCode.ERROR_ERROR_TYPE_NOT_FOUND, id);
    }
}
