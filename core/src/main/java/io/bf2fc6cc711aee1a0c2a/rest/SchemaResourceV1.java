package io.bf2fc6cc711aee1a0c2a.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/v1")
public interface SchemaResourceV1 {

    /**
     *
     */
    @Path("/")
    @GET
    @Produces("application/json")
    String getSchema();
}
