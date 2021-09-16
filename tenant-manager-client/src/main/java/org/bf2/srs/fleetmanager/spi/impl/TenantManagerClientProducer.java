package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.rest.client.auth.OidcAuth;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Optional;

@ApplicationScoped
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.server-url.configured")
    String tenantManagerAuthServerUrl;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.client-id")
    String tenantManagerAuthClientId;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.secret")
    String tenantManagerAuthSecret;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.enabled")
    boolean tenantManagerAuthEnabled;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public TenantManagerService produce() {
        if (tenantManagerAuthEnabled) {
            log.info("Using Apicurio Registry REST TenantManagerClient with authentication enabled.");
            return new RestClientTenantManagerServiceImpl(new OidcAuth(tenantManagerAuthServerUrl, tenantManagerAuthClientId, tenantManagerAuthSecret, Optional.empty()));
        } else {
            log.info("Using Apicurio Registry REST TenantManagerClient.");
            return new RestClientTenantManagerServiceImpl();
        }
    }
}
