package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

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
 * Manage the list of all registry deployments.
 * Manage a specific registry deployment.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/v1/admin/registryDeployments")
public interface RegistryDeploymentsResourceV1 {

    /**
     * Get the list of all registry deployments.
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<RegistryDeploymentRest> getRegistryDeployments();

    /**
     * Create a registry deployment.
     */
    @Path("/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Response createRegistryDeployment(@Valid RegistryDeploymentCreateRest data) throws StorageConflictException;

    /**
     * Get a specific registry deployment.
     */
    @Path("/{registryDeploymentId}")
    @GET
    @Produces("application/json")
    RegistryDeploymentRest getRegistryDeployment(@PathParam("registryDeploymentId") Long registryDeploymentId) throws RegistryDeploymentNotFoundException;

    /**
     * Delete a specific Registry Deployment.
     */
    @Path("/{registryDeploymentId}")
    @DELETE
    void deleteRegistryDeployment(@PathParam("registryDeploymentId") Long registryDeploymentId) throws RegistryDeploymentNotFoundException, StorageConflictException;
}
