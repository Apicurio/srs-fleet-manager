package org.bf2.srs.fleetmanager.rest.config;

import javax.ws.rs.core.Response;

public interface CustomExceptionMapper {


    boolean supportsPath(String path);

    Response toResponse(Throwable exception);
}
