package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import com.fasterxml.jackson.core.JsonParseException;
import io.quarkus.runtime.configuration.ProfileManager;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.bf2.srs.fleetmanager.errors.UserErrorMapper;
import org.bf2.srs.fleetmanager.rest.config.CustomExceptionMapper;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.service.model.Kind;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionMapperImpl.class);

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
    public boolean supportsPath(String path) {
        return path.startsWith("/api/serviceregistry_mgmt/v1") && !path.startsWith("/api/serviceregistry_mgmt/v1/admin");
    }

    @Override
    public Response toResponse(Throwable exception) {
        Response.ResponseBuilder builder;

        int code;
        if (exception instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) exception;
            Response response = wae.getResponse();
            builder = Response.fromResponse(response);
        } else {
            code = CODE_MAP.getOrDefault(exception.getClass(), HTTP_INTERNAL_ERROR);
            builder = Response.status(code);
        }

        UserErrorInfo ei = null;
        if(exception instanceof UserError) {
            ei = ((UserError) exception).getUserErrorInfo();
        } else if(exception instanceof Exception && UserErrorMapper.hasMapping(((Exception)exception).getClass())) {
            ei = UserErrorMapper.getMapping((Exception)exception);
        }else{
            ei = UserErrorInfo.create(UserErrorCode.ERROR_UNKNOWN);
            log.warn("Processing an unknown error", exception);
        }
        Error errorInfo = new Error();
        errorInfo.setKind(Kind.ERROR);
        errorInfo.setId(Integer.toString(ei.getCode().getId()));
        errorInfo.setHref("/api/serviceregistry_mgmt/v1/errors/" + errorInfo.getId());
        errorInfo.setCode(ei.getCode().getCode());
        errorInfo.setReason(ei.getReason());
        // errorInfo.setOperationId(""); TODO


        if (!"prod".equals(quarkusProfile)) {
            var extendedReason = errorInfo.getReason();
            extendedReason += ". Details:\n";
            extendedReason += exception.getClass().getCanonicalName() + ": " + exception.getMessage() + "\n";

            StringWriter sw = new StringWriter(); // No need to close
            PrintWriter pw = new PrintWriter(sw); // No need to close
            exception.printStackTrace(pw);
            extendedReason += "Stack Trace:\n" + sw.toString();

            errorInfo.setReason(extendedReason);
        }

        return builder.type(MediaType.APPLICATION_JSON)
                .entity(errorInfo)
                .build();
    }
}
