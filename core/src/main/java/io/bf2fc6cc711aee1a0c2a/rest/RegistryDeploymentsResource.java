package io.bf2fc6cc711aee1a0c2a.rest;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.CreateRegistryDeploymentTask;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertRegistryDeployment;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static java.util.stream.Collectors.toList;

@Path("/api/v1/admin/registry-deployments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistryDeploymentsResource { // TODO interface?

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @POST
    public void createRegistryDeployment(RegistryDeploymentRest registryDeployment) {
        tasks.submit(CreateRegistryDeploymentTask.builder()
                .registryDeployment(registryDeployment)
                .build());
    }

    @GET
    public List<RegistryDeploymentRest> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    ///// "/{id}"

    @GET
    @Path("/{id}")
    public RegistryDeploymentRest getRegistryDeployment(@PathParam("id") Long id) {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElse(null); // TODO HTTP status
    }

    @DELETE
    @Path("/{id}")
    public void deleteRegistryDeployment(@PathParam("id") Long id) {
        storage.deleteRegistryDeployment(id);
    }
}
