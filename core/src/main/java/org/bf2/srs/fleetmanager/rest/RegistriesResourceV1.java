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
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/v1/registries")
public interface RegistriesResourceV1 {

    /**
     *
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<RegistryRest> getRegistries();

    /**
     *
     */
    @Path("/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Response createRegistry(@Valid RegistryCreateRest registryCreate) throws StorageConflictException;

    /**
     * Get a single `Registry`.
     */
    @Path("/{registryId}")
    @GET
    @Produces("application/json")
    RegistryRest getRegistry(@PathParam("registryId") Long registryId) throws RegistryNotFoundException;

    /**
     * Delete an existing `Registry`.
     */
    @Path("/{registryId}")
    @DELETE
    void deleteRegistry(@PathParam("registryId") Long registryId) throws RegistryNotFoundException;
}
