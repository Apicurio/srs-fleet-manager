package org.bf2.srs.fleetmanager.spi;

import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;

public class TermsRequiredException extends Exception implements UserError {

    private final String accountId;

    public TermsRequiredException(String accountId) {
        super();
        this.accountId = accountId;
    }

    @Override
    public String getMessage() {
        return getUserErrorInfo().getReason();
    }

    @Override
    public UserErrorInfo getUserErrorInfo() {
        return UserErrorInfo.create(UserErrorCode.ERROR_AMS_TERMS_NOT_ACCEPTED, accountId);
    }
}
