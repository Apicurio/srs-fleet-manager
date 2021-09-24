package org.bf2.srs.fleetmanager.spi.impl.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apicurio.rest.client.error.ApicurioRestClientException;
import io.apicurio.rest.client.error.RestClientErrorHandler;
import io.apicurio.rest.client.util.IoUtil;
import org.bf2.srs.fleetmanager.spi.impl.model.response.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class AccountManagementErrorHandler implements RestClientErrorHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ApicurioRestClientException handleErrorResponse(InputStream inputStream, int statusCode) {
        String res = IoUtil.toString(inputStream);
        try {
            // Try to parse it as an Error entity
            Error error = MAPPER.readValue(res, Error.class);
            return new AccountManagementSystemClientException(error, statusCode);
        } catch (JsonProcessingException e) {
            // Ignore and use the raw string
            log.debug("Could not parse Error entity from AMS response", e);
            return new AccountManagementSystemClientException(res);
        }
    }

    @Override
    public ApicurioRestClientException parseError(Exception e) {
        throw new AccountManagementSystemClientException(e);
    }

    @Override
    public ApicurioRestClientException parseInputSerializingError(JsonProcessingException e) {
        throw new AccountManagementSystemClientException(e);
    }
}
