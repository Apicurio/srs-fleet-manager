package org.b2f.ams.client.exception;

import io.apicurio.rest.client.error.ApicurioRestClientException;
import org.b2f.ams.client.model.response.Error;

public class AccountManagementSystemClientException extends ApicurioRestClientException {

    private static final long serialVersionUID = 1L;

    public AccountManagementSystemClientException(Error error) {
        super(String.format("Error found when executing action with kind: %s, code: %s, href: %s, id: %s, operationId: %s, and reason: %s",
                error.getKind(), error.getCode(), error.getHref(), error.getId(), error.getOperationId(), error.getReason()));
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
    }

    public AccountManagementSystemClientException(Throwable error) {
        super(error.getMessage());
    }
}
