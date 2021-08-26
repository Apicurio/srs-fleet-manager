package org.bf2.srs.fleetmanager.rest.publicapi;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.service.ErrorNotFoundException;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/api")
public interface ApiResource {
    /**
     *
     */
    @Path("/serviceregistry_mgmt/v1/registries")
    @GET
    @Produces("application/json")
    RegistryList getRegistries(@Min(1) @QueryParam("page") Integer page, @Min(1) @Max(500) @QueryParam("size") Integer size,
                               @QueryParam("orderBy") String orderBy, @QueryParam("search") String search);

    /**
     *
     */
    @Path("/serviceregistry_mgmt/v1/registries")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Registry createRegistry(RegistryCreate data) throws RegistryStorageConflictException, TermsRequiredException, ResourceLimitReachedException;

    /**
     * Gets the details of a single instance of a `Registry`.
     */
    @Path("/serviceregistry_mgmt/v1/registries/{id}")
    @GET
    @Produces("application/json")
    Registry getRegistry(@PathParam("id") String id) throws RegistryNotFoundException;

    /**
     * Deletes an existing `Registry`.
     */
    @Path("/serviceregistry_mgmt/v1/registries/{id}")
    @DELETE
    void deleteRegistry(@PathParam("id") String id) throws RegistryStorageConflictException, RegistryNotFoundException;

    /**
     *
     */
    @Path("/serviceregistry_mgmt/v1/errors/{id}")
    @GET
    @Produces("application/json")
    Error getError(@PathParam("id") Integer id) throws ErrorNotFoundException;

    @Path("/serviceregistry_mgmt/v1/errors")
    @GET
    @Produces("application/json")
    ErrorList getErrors(@Min(1) @QueryParam("page") Integer page, @Min(1) @Max(500) @QueryParam("size") Integer size);
}
