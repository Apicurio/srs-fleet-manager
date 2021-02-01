package io.bf2fc6cc711aee1a0c2a.rest;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.CreateRegistryTask;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertRegistry;
import io.bf2fc6cc711aee1a0c2a.rest.model.CreateRegistryRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryRest;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryNotFoundException;
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

@Path("/api/v1/registries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistriesResource {


    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistry convertRegistry;

    @POST
    public void createRegistry(CreateRegistryRest registry) {
        tasks.submit(CreateRegistryTask.builder()
                .registry(registry)
                .build());
    }

    @GET
    public List<RegistryRest> getRegistries() {
        return storage.getAllRegistries().stream()
                .map(convertRegistry::convert)
                .collect(toList());
    }

    ///// "/{id}"

    @GET
    @Path("/{id}")
    public RegistryRest getRegistry(@PathParam("id") Long id) {
        return storage.getRegistryById(id)
                .map(convertRegistry::convert)
                .orElse(null); // TODO HTTP status
    }

    @DELETE
    @Path("/{id}")
    public void deleteRegistry(@PathParam("id") Long id) throws RegistryNotFoundException {
        storage.deleteRegistry(id);
    }
}
