package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRestList;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Manage the list of all registries.
 * Manage a specific Registry.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/serviceregistry_mgmt/v1/registries")
public interface RegistriesResourceV1 {

    /**
     * Get the list of all registries.
     */
    @Path("/")
    @GET
    @Produces("application/json")
    // TODO QueryParam as model instead of arguments
    RegistryRestList getRegistries(@QueryParam("page") Integer page, @QueryParam("size") Integer size, @QueryParam("orderBy") String orderBy, @QueryParam("search") String search);

    /**
     * Create a Registry.
     */
    @Path("/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Response createRegistry(@Valid RegistryCreateRest registryCreate) throws StorageConflictException;

    /**
     * Get a Registry
     *
     * Gets the details of a single instance of a `Registry`.
     */
    @Path("/{id}")
    @GET
    @Produces("application/json")
    RegistryRest getRegistry(@PathParam("id") String registryId) throws RegistryNotFoundException;

    /**
     * Delete a Registry
     *
     * Deletes an existing `Registry`.
     */
    @Path("/{id}")
    @DELETE
    void deleteRegistry(@PathParam("id") String registryId) throws RegistryNotFoundException, StorageConflictException;
}
