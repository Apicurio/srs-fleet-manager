package org.bf2.srs.fleetmanager.rest.privateapi;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.bf2.srs.fleetmanager.execution.manager.TaskNotFoundException;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.TaskRest;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/api")
public interface ApiResource {
  @Path("/serviceregistry_mgmt/v1/admin/tasks")
  @GET
  @Produces("application/json")
  List<TaskRest> getTasks();

  @Path("/serviceregistry_mgmt/v1/admin/tasks/{taskId}")
  @GET
  @Produces("application/json")
  TaskRest getTask(@PathParam("taskId") String taskId) throws TaskNotFoundException;

  @Path("/serviceregistry_mgmt/v1/admin/registryDeployments")
  @GET
  @Produces("application/json")
  List<RegistryDeploymentRest> getRegistryDeployments();

  @Path("/serviceregistry_mgmt/v1/admin/registryDeployments")
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  RegistryDeploymentRest createRegistryDeployment(RegistryDeploymentCreateRest data) throws StorageConflictException;

  @Path("/serviceregistry_mgmt/v1/admin/registryDeployments/{registryDeploymentId}")
  @GET
  @Produces("application/json")
  RegistryDeploymentRest getRegistryDeployment(
      @PathParam("registryDeploymentId") Integer registryDeploymentId) throws RegistryDeploymentNotFoundException;

  @Path("/serviceregistry_mgmt/v1/admin/registryDeployments/{registryDeploymentId}")
  @DELETE
  void deleteRegistryDeployment(@PathParam("registryDeploymentId") Integer registryDeploymentId) throws StorageConflictException, RegistryDeploymentNotFoundException;

  @Path("/serviceregistry_mgmt/v1/admin/openapi")
  @GET
  @Produces("application/json")
  String getSchema();
}
