package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import com.fasterxml.jackson.core.JsonParseException;
import io.quarkus.runtime.configuration.ProfileManager;
import org.bf2.srs.fleetmanager.common.errors.UserError;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.errors.UserErrorInfo;
import org.bf2.srs.fleetmanager.errors.UserErrorMapper;
import org.bf2.srs.fleetmanager.metrics.ExceptionMetrics;
import org.bf2.srs.fleetmanager.rest.config.CustomExceptionMapper;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.service.ErrorNotFoundException;
import org.bf2.srs.fleetmanager.rest.service.model.Kind;
import org.bf2.srs.fleetmanager.spi.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.TooManyInstancesException;
import org.bf2.srs.fleetmanager.spi.impl.exception.AccountManagementSystemClientException;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotSupportedException;
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

    @Inject
    ExceptionMetrics exceptionMetrics;

    static {

        Map<Class<? extends Exception>, Integer> map = new HashMap<>();

        map.put(RegistryNotFoundException.class, HTTP_NOT_FOUND);
        map.put(RegistryDeploymentNotFoundException.class, HTTP_NOT_FOUND);
        map.put(ErrorNotFoundException.class, HTTP_NOT_FOUND);

        map.put(DateTimeParseException.class, HTTP_BAD_REQUEST);
        map.put(ConstraintViolationException.class, HTTP_BAD_REQUEST);
        map.put(JsonParseException.class, HTTP_BAD_REQUEST);

        map.put(NotSupportedException.class, HTTP_UNSUPPORTED_TYPE);

        map.put(AccountManagementSystemClientException.class, HTTP_INTERNAL_ERROR);
        map.put(EvalInstancesNotAllowedException.class, HTTP_INTERNAL_ERROR);

        map.put(RegistryStorageConflictException.class, HTTP_CONFLICT);
        map.put(ResourceLimitReachedException.class, HTTP_CONFLICT);
        map.put(TermsRequiredException.class, HTTP_CONFLICT);
        map.put(TooManyEvalInstancesForUserException.class, HTTP_CONFLICT);

        map.put(TooManyInstancesException.class, HTTP_PAYMENT_REQUIRED);

        CODE_MAP = Collections.unmodifiableMap(map);
    }

    @Override
    public boolean supportsPath(String path) {
        return path.startsWith("/api/serviceregistry_mgmt/v1") && !path.startsWith("/api/serviceregistry_mgmt/v1/admin");
    }

    @Override
    public Response toResponse(Throwable exception) {

        exceptionMetrics.record(exception);

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

        UserErrorInfo uei = null;
        if (exception instanceof UserError) {
            uei = ((UserError) exception).getUserErrorInfo();
        } else if (exception instanceof Exception && UserErrorMapper.hasMapping(((Exception) exception).getClass())) {
            uei = UserErrorMapper.getMapping((Exception) exception);
        } else {
            uei = UserErrorInfo.create(UserErrorCode.ERROR_UNKNOWN);
        }

        if (uei.getCode() == UserErrorCode.ERROR_UNKNOWN) {
            // This will also handle unknown errors that have been deliberately returned
            log.warn("Processing an unknown exception (no specific user error has been defined)", exception);
        }

        Error ei = new Error();
        ei.setKind(Kind.ERROR);
        ei.setId(Integer.toString(uei.getCode().getId()));
        ei.setHref("/api/serviceregistry_mgmt/v1/errors/" + ei.getId());
        ei.setCode(uei.getCode().getCode());
        ei.setReason(uei.getReason());
        // ei.setOperationId(""); TODO

        if (!"prod".equals(quarkusProfile)) {
            var extendedReason = ei.getReason();
            extendedReason += ". Details:\n";
            extendedReason += exception.getClass().getCanonicalName() + ": " + exception.getMessage() + "\n";

            StringWriter sw = new StringWriter(); // No need to close
            PrintWriter pw = new PrintWriter(sw); // No need to close
            exception.printStackTrace(pw);
            extendedReason += "Stack Trace:\n" + sw.toString();

            ei.setReason(extendedReason);
        }

        return builder.type(MediaType.APPLICATION_JSON)
                .entity(ei)
                .build();
    }
}
