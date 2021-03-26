package org.b2f.ams.client.exception;

import org.b2f.ams.client.model.response.Error;

public class AccountManagementSystemClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Error error;

    public AccountManagementSystemClientException() {
        super();
    }

    public AccountManagementSystemClientException(Error error) {
        super(String.format("Error found when executing action with kind: %s, code: %s, href: %s, id: %s, operationId: %s, and reason: %s",
                error.getKind(), error.getCode(), error.getHref(), error.getId(), error.getOperationId(), error.getReason()));
    }

    public AccountManagementSystemClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AccountManagementSystemClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountManagementSystemClientException(String message) {
        super(message);
    }

    public AccountManagementSystemClientException(Throwable cause) {
        super(cause);
    }

}
