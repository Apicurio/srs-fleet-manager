package org.bf2.srs.fleetmanager.spi.impl.exception;

import org.bf2.srs.fleetmanager.spi.impl.model.response.Error;

public class SubscriptionNotFoundAMSCException extends AccountManagementSystemClientException {

    private static final long serialVersionUID = 7944430394460196319L;

    public SubscriptionNotFoundAMSCException(Error causeEntity, int statusCode) {
        super(causeEntity, statusCode);
    }

    public SubscriptionNotFoundAMSCException(String message, int statusCode) {
        super(message, statusCode);
    }
}
