package org.bf2.srs.fleetmanager.spi.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apicurio.rest.client.auth.exception.AuthErrorHandler;
import io.apicurio.rest.client.error.ApicurioRestClientException;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemClientException;

import java.io.InputStream;

public class AccountManagementSystemErrorHandler extends AuthErrorHandler {
    @Override
    public ApicurioRestClientException handleErrorResponse(InputStream inputStream, int i) {
        return super.handleErrorResponse(inputStream, i);
    }

    @Override
    public ApicurioRestClientException parseError(Exception e) {
        return new AccountManagementSystemClientException(e);
    }

    @Override
    public ApicurioRestClientException parseInputSerializingError(JsonProcessingException e) {
        return new AccountManagementSystemClientException(e);
    }
}
