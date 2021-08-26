package org.bf2.srs.fleetmanager.rest.privateapi.impl;

import com.fasterxml.jackson.core.JsonParseException;
import io.quarkus.runtime.configuration.ProfileManager;
import org.bf2.srs.fleetmanager.rest.config.CustomExceptionMapper;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.ErrorInfo1Rest;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.net.HttpURLConnection.*;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class CustomExceptionMapperImpl implements CustomExceptionMapper {

    private static final Map<Class<? extends Exception>, Integer> CODE_MAP;

    private String quarkusProfile = ProfileManager.getActiveProfile();

    static {

        Map<Class<? extends Exception>, Integer> map = new HashMap<>();

        map.put(RegistryNotFoundException.class, HTTP_NOT_FOUND);
        map.put(RegistryDeploymentNotFoundException.class, HTTP_NOT_FOUND);

        map.put(DateTimeParseException.class, HTTP_BAD_REQUEST);
        map.put(ConstraintViolationException.class, HTTP_BAD_REQUEST);
        map.put(JsonParseException.class, HTTP_BAD_REQUEST);

        map.put(RegistryDeploymentStorageConflictException.class, HTTP_CONFLICT);

        CODE_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public boolean supportsPath(String path) {
        return path.startsWith("/api/serviceregistry_mgmt/v1/admin");
    }

    @Override
    public Response toResponse(Throwable exception) {
        Response.ResponseBuilder builder;
        int code;
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            Response response = wae.getResponse();
            builder = Response.fromResponse(response);
            code = response.getStatus();
        } else {
            code = CODE_MAP.getOrDefault(exception.getClass(), HTTP_INTERNAL_ERROR);
            builder = Response.status(code);
        }

        ErrorInfo1Rest errorInfo = new ErrorInfo1Rest();
        errorInfo.setErrorCode(code);

        if ("prod".equals(quarkusProfile)) {
            errorInfo.setMessage(exception.getClass().getCanonicalName() + ": " + exception.getMessage());
        } else {
            StringWriter sw = new StringWriter(); // No need to close
            PrintWriter pw = new PrintWriter(sw); // No need to close
            exception.printStackTrace(pw);
            errorInfo.setMessage(exception.getClass().getCanonicalName() + ": " +
                    exception.getMessage() + "\nStack Trace:\n" + sw.toString());
        }

        return builder.type(MediaType.APPLICATION_JSON)
                .entity(errorInfo)
                .build();
    }
}
