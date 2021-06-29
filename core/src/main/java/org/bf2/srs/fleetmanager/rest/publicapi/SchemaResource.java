package org.bf2.srs.fleetmanager.rest.publicapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 */
@Path("/api/serviceregistry_mgmt/v1/openapi")
public interface SchemaResource {

    @Path("/")
    @GET
    @Produces("application/json")
    String getSchema();
}
