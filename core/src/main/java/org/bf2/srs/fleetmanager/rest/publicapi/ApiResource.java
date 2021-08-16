package org.bf2.srs.fleetmanager.rest.publicapi;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

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
  RegistryListRest getRegistries(@Min(1)  @QueryParam("page") Integer page, @Min(1) @Max(1000) @QueryParam("size") Integer size,
      @QueryParam("orderBy") String orderBy, @QueryParam("search") String search);

  /**
   *
   */
  @Path("/serviceregistry_mgmt/v1/registries")
  @POST
  @Produces("application/json")
  @Consumes("application/json")
  RegistryRest createRegistry(RegistryCreateRest data) throws StorageConflictException, TermsRequiredException, ResourceLimitReachedException;

  /**
   * Gets the details of a single instance of a `Registry`.
   */
  @Path("/serviceregistry_mgmt/v1/registries/{id}")
  @GET
  @Produces("application/json")
  RegistryRest getRegistry(@PathParam("id") String id) throws RegistryNotFoundException;

  /**
   * Deletes an existing `Registry`.
   */
  @Path("/serviceregistry_mgmt/v1/registries/{id}")
  @DELETE
  void deleteRegistry(@PathParam("id") String id) throws StorageConflictException, RegistryNotFoundException;
}
