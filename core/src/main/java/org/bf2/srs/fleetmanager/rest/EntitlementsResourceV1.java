package org.bf2.srs.fleetmanager.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public interface EntitlementsResourceV1 {

    /**
     *
     */
    @Path("/entitlements/exists")
    @GET
    @Produces("application/json")
    public boolean hasEntitlements(String clusterId);
}
