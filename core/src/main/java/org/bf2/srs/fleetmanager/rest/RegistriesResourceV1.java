package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Manage the list of all registries.
 * Manage a specific Registry.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/v1/registries")
public interface RegistriesResourceV1 {

    /**
     * Get the list of all registries.
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<RegistryRest> getRegistries();

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
    @Path("/{registryId}")
    @GET
    @Produces("application/json")
    RegistryRest getRegistry(@PathParam("registryId") Long registryId) throws RegistryNotFoundException;

    /**
     * Delete a Registry
     *
     * Deletes an existing `Registry`.
     */
    @Path("/{registryId}")
    @DELETE
    void deleteRegistry(@PathParam("registryId") Long registryId) throws RegistryNotFoundException, StorageConflictException;
}
