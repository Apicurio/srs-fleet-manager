package org.bf2.srs.fleetmanager.rest.config;

import com.fasterxml.jackson.core.JsonParseException;
import org.bf2.srs.fleetmanager.rest.model.ErrorInfoRest;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import io.quarkus.runtime.configuration.ProfileManager;

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
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.net.HttpURLConnection.*;

@ApplicationScoped
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Map<Class<? extends Exception>, Integer> CODE_MAP;

    private String quarkusProfile = ProfileManager.getActiveProfile();

    static {
        Map<Class<? extends Exception>, Integer> map = new HashMap<>();

        map.put(RegistryNotFoundException.class, HTTP_NOT_FOUND);
        map.put(RegistryDeploymentNotFoundException.class, HTTP_NOT_FOUND);

        map.put(DateTimeParseException.class, HTTP_BAD_REQUEST);
        map.put(ConstraintViolationException.class, HTTP_BAD_REQUEST);
        map.put(JsonParseException.class, HTTP_BAD_REQUEST);

        map.put(StorageConflictException.class, HTTP_CONFLICT);

        CODE_MAP = Collections.unmodifiableMap(map);
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

        ErrorInfoRest.ErrorInfoRestBuilder errorInfoBuilder = ErrorInfoRest.builder()
                .errorCode(code);

        if ("prod".equals(quarkusProfile)) {
            errorInfoBuilder.message(exception.getClass().getCanonicalName() + ": " + exception.getMessage());
        } else {
            StringWriter sw = new StringWriter(); // No need to close
            PrintWriter pw = new PrintWriter(sw); // No need to close
            exception.printStackTrace(pw);
            errorInfoBuilder.message(exception.getClass().getCanonicalName() + ": " +
                    exception.getMessage() + "\nStack Trace:\n" + sw.toString());
        }

        return builder.type(MediaType.APPLICATION_JSON)
                .entity(errorInfoBuilder.build())
                .build();
    }
}
