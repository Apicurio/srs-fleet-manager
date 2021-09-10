package org.bf2.srs.fleetmanager.rest.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * "When choosing an exception mapping provider to map an exception,
 * an implementation MUST use the provider whose generic type is the nearest superclass of the exception."
 * <p>
 * Since Quarkus already defines {@link io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyViolationExceptionMapper},
 * it is used before our {@link org.bf2.srs.fleetmanager.rest.config.CommonExceptionMapper} has a chance to process it.
 * <p>
 * Therefore, this is a workaround to force the exception processing using our mapper.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    CommonExceptionMapper common;

    @Override
    public Response toResponse(ValidationException exception) {
        return common.toResponse(exception);
    }
}
