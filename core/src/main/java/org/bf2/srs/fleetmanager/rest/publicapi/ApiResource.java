package org.bf2.srs.fleetmanager.rest.publicapi;

import org.bf2.srs.fleetmanager.auth.NotAuthorizedException;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ServiceStatus;
import org.bf2.srs.fleetmanager.rest.service.ErrorNotFoundException;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.TooManyInstancesException;

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

/**
 * A JAX-RS interface.  An implementation of this interface must be provided.
 */
@Path("/api")
public interface ApiResource {
    /**
     * Get the list of all Registry instances
     */
    @Path("/serviceregistry_mgmt/v1/registries")
    @GET
    @Produces("application/json")
    RegistryList getRegistries(@Min(1) @QueryParam("page") Integer page, @Min(1) @Max(500) @QueryParam("size") Integer size,
                               @QueryParam("orderBy") String orderBy, @QueryParam("search") String search);

    /**
     * Create a new Registry instance
     */
    @Path("/serviceregistry_mgmt/v1/registries")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    Registry createRegistry(RegistryCreate data) throws RegistryStorageConflictException, TermsRequiredException, ResourceLimitReachedException,
            EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException, TooManyInstancesException,
            AccountManagementServiceException;

    /**
     * Gets the details of a single instance of a `Registry`.
     */
    @Path("/serviceregistry_mgmt/v1/registries/{id}")
    @GET
    @Produces("application/json")
    Registry getRegistry(@PathParam("id") String id) throws RegistryNotFoundException, NotAuthorizedException;

    /**
     * Deletes an existing `Registry` instance and all of the data that it stores. Important: Users should export the registry data before deleting the instance, e.g., using the Service Registry web console, core REST API, or `rhoas` CLI.
     */
    @Path("/serviceregistry_mgmt/v1/registries/{id}")
    @DELETE
    void deleteRegistry(@PathParam("id") String id) throws RegistryStorageConflictException, RegistryNotFoundException, NotAuthorizedException;

    /**
     * Get information about a specific error type
     */
    @Path("/serviceregistry_mgmt/v1/errors/{id}")
    @GET
    @Produces("application/json")
    Error getError(@PathParam("id") Integer id) throws ErrorNotFoundException;

    /**
     * Get the list of all errors
     */
    @Path("/serviceregistry_mgmt/v1/errors")
    @GET
    @Produces("application/json")
    ErrorList getErrors(@Min(1) @QueryParam("page") Integer page, @Min(1) @Max(500) @QueryParam("size") Integer size);

    /**
     * Get the service status
     */
    @Path("/serviceregistry_mgmt/v1/status")
    @GET
    @Produces("application/json")
    ServiceStatus getServiceStatus();
}
