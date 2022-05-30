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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.ValidationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.net.HttpURLConnection.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class CustomExceptionMapperImpl implements CustomExceptionMapper {

    private static final Map<Class<? extends Exception>, Integer> CODE_MAP;

    private final String quarkusProfile = ProfileManager.getActiveProfile();

    static {
        // NOTE: Subclasses of the entry will be matched as well.
        // Make sure that if a more specific exception requires a different error code,
        // it is inserted first.
        Map<Class<? extends Exception>, Integer> map = new LinkedHashMap<>();

        map.put(RegistryNotFoundException.class, HTTP_NOT_FOUND);
        map.put(RegistryDeploymentNotFoundException.class, HTTP_NOT_FOUND);

        map.put(DateTimeParseException.class, HTTP_BAD_REQUEST);
        map.put(ValidationException.class, HTTP_BAD_REQUEST);
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

        Optional<Integer> code = empty();
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            Response response = wae.getResponse();
            builder = Response.fromResponse(response);
            code = Optional.of(response.getStatus());
        } else {
            // Test for subclasses

            for (Map.Entry<Class<? extends Exception>, Integer> entry : CODE_MAP.entrySet()) {
                if (entry.getKey().isAssignableFrom(exception.getClass())) {
                    code = of(entry.getValue());
                }
            }
            builder = Response.status(code.orElse(HTTP_INTERNAL_ERROR));
        }

        ErrorInfo1Rest errorInfo = new ErrorInfo1Rest();
        errorInfo.setErrorCode(code.orElse(HTTP_INTERNAL_ERROR));

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
