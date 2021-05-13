package org.bf2.srs.fleetmanager.rest.impl;

import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.rest.EntitlementsResourceV1;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EntitlementsResourceV1Impl implements EntitlementsResourceV1 {

    @Inject
    public AuthService authService;

    @Inject
    public AccountManagementService accountManagementService;

    @Override
    public boolean hasEntitlements(String clusterId) {

        //TODO fill resource type

        return accountManagementService.hasEntitlements(authService.extractAccountInfo(), "", clusterId);
    }
}
