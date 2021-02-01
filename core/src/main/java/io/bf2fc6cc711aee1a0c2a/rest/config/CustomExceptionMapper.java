package io.bf2fc6cc711aee1a0c2a.rest.config;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.bf2fc6cc711aee1a0c2a.storage.RegistryDeploymentNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryNotFoundException;

@ApplicationScoped
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Map<Class<? extends Exception>, Integer> CODE_MAP;

    static {
        Map<Class<? extends Exception>, Integer> map = new HashMap<>();
        map.put(RegistryNotFoundException.class, HTTP_NOT_FOUND);
        map.put(RegistryDeploymentNotFoundException.class, HTTP_NOT_FOUND);
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


        //TODO return nice json generic error
        Map<String, Object> error = new HashMap<>();
        error.put("message", exception.getMessage());
        error.put("error_code", code);
        return builder.type(MediaType.APPLICATION_JSON)
                      .entity(error)
                      .build();
    }

}
