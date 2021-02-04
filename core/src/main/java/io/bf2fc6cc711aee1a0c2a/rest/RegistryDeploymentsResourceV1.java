package io.bf2fc6cc711aee1a0c2a.rest;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentCreateRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryDeploymentNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;

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
@Path("/api/v1/registryDeployments")
public interface RegistryDeploymentsResourceV1 {

    /**
     *
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<RegistryDeploymentRest> getRegistryDeployments();

    /**
     *
     */
    @Path("/")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Response createRegistryDeployment(@Valid RegistryDeploymentCreateRest data) throws StorageConflictException;

    /**
     *
     */
    @Path("/{registryDeploymentId}")
    @GET
    @Produces("application/json")
    RegistryDeploymentRest getRegistryDeployment(@PathParam("registryDeploymentId") Long registryDeploymentId) throws RegistryDeploymentNotFoundException;

    /**
     *
     */
    @Path("/{registryDeploymentId}")
    @DELETE
    void deleteRegistryDeployment(@PathParam("registryDeploymentId") Long registryDeploymentId) throws RegistryDeploymentNotFoundException;
}
