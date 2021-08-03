package org.b2f.ams.client.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apicurio.rest.client.error.ApicurioRestClientException;
import io.apicurio.rest.client.error.RestClientErrorHandler;
import io.apicurio.rest.client.util.IoUtil;

import java.io.InputStream;

public class AccountManagementErrorHandler implements RestClientErrorHandler {

    @Override
    public ApicurioRestClientException handleErrorResponse(InputStream inputStream, int i) {
        return new AccountManagementSystemClientException(IoUtil.toString(inputStream));
    }

    @Override
    public ApicurioRestClientException parseError(Exception e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApicurioRestClientException parseInputSerializingError(JsonProcessingException e) {
        throw new UnsupportedOperationException();
    }
}
