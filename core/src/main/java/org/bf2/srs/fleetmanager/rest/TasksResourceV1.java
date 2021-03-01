package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.model.TaskRest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/v1/admin/tasks")
public interface TasksResourceV1 {

    /**
     *
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<TaskRest> getTasks();

    /**
     *
     */
    @Path("/{taskId}")
    @GET
    @Produces("application/json")
    TaskRest getTask(@PathParam("taskId") String id) throws TaskNotFoundException;
}
