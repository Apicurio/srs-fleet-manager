package org.bf2.srs.fleetmanager.spi.impl.exception;

public class QuotaExceededException extends AccountManagementSystemClientException {

    public QuotaExceededException(String accountId) {
        super(String.format("Terms not accepted by account with id %s", accountId));
    }
}
