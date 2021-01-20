package io.bf2fc6cc711aee1a0c2a.rest;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertTask;
import io.bf2fc6cc711aee1a0c2a.rest.model.TaskRest;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/admin/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TasksResource {

    @Inject
    TaskManager taskManager;

    @Inject
    ConvertTask convertTask;

    @GET
    public List<TaskRest> getAllTasks() {
        return taskManager.getAllTasks().stream().map(convertTask::convert).collect(Collectors.toList());
    }

    ///// "/{id}"

    @GET
    @Path("/{id}")
    public Task getRegistryDeployment(@PathParam("id") String id) {
        return null;
    }
}
