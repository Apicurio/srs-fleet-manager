package org.bf2.srs.fleetmanager.rest;

import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.model.TaskRest;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Manage the list of all tasks executed on the server.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Path("/api/serviceregistry_mgmt/v1/admin/tasks")
public interface TasksResourceV1 {

    /**
     * Get the list of all tasks executed on the server.
     */
    @Path("/")
    @GET
    @Produces("application/json")
    List<TaskRest> getTasks();

    /**
     * Get a specific task executed on the server.
     */
    @Path("/{taskId}")
    @GET
    @Produces("application/json")
    TaskRest getTask(@PathParam("taskId") String id) throws TaskNotFoundException;
}
