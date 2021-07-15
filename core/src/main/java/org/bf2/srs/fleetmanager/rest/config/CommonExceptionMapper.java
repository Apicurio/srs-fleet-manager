package org.bf2.srs.fleetmanager.rest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
@Provider
public class CommonExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Context
    UriInfo uriInfo;

    @Inject
    Instance<CustomExceptionMapper> mappers;

    @Override
    public Response toResponse(Throwable exception) {
        Optional<CustomExceptionMapper> mapper = mappers.stream().filter(m -> m.supportsPath(uriInfo.getPath())).findFirst();
        if (mapper.isPresent()) {
            Response r = mapper.get().toResponse(exception);
            if (r.getStatusInfo().getFamily() == Family.SERVER_ERROR) {
                log.error("Unhandled exception", exception);
            }
            return r;
        } else {
            log.error("No custom exception mapper available for path " + uriInfo.getPath(), exception);
            return Response.serverError().build();
        }
    }
}
