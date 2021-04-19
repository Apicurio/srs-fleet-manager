package org.b2f.ams.client.exception;

public class TermsRequiredException extends AccountManagementSystemClientException {

    public TermsRequiredException(String accountId) {
        super(String.format("Terms not accepted by account with id %s", accountId));
    }
}
