package org.bf2.srs.fleetmanager.rest.publicapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ServiceStatus;

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/api")
public interface ApiResource {
  /**
   *
   */
  @Path("/serviceregistry_mgmt/v1/registries")
  @GET
  @Produces("application/json")
  RegistryList getRegistries(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
      @QueryParam("orderBy") String orderBy, @QueryParam("search") String search);

  /**
   *
   */
  @Path("/serviceregistry_mgmt/v1/registries")
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  Registry createRegistry(RegistryCreate data);

  /**
   * Gets the details of a single instance of a `Registry`.
   */
  @Path("/serviceregistry_mgmt/v1/registries/{id}")
  @GET
  @Produces("application/json")
  Registry getRegistry(@PathParam("id") String id);

  /**
   * Deletes an existing `Registry` instance and all of the data that it stores. Important: Users should export the registry data before deleting the instance, e.g., using the Service Registry web console or the Apicurio Registry core REST API.
   */
  @Path("/serviceregistry_mgmt/v1/registries/{id}")
  @DELETE
  void deleteRegistry(@PathParam("id") String id);

  @Path("/serviceregistry_mgmt/v1/errors")
  @GET
  @Produces("application/json")
  ErrorList getErrors(@QueryParam("page") Integer page, @QueryParam("size") Integer size);

  @Path("/serviceregistry_mgmt/v1/status")
  @GET
  @Produces("application/json")
  ServiceStatus getServiceStatus();

  /**
   *
   */
  @Path("/serviceregistry_mgmt/v1/errors/{id}")
  @GET
  @Produces("application/json")
  Error getError(@PathParam("id") Integer id);
}
