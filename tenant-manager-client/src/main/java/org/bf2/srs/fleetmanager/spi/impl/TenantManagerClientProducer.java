package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.multitenant.client.Auth;
import io.apicurio.multitenant.client.TenantManagerClientImpl;
import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.server-url")
    String tenantManagerAuthServerUrl;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.client-id")
    String tenantManagerAuthClientId;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.secret")
    String tenantManagerAuthSecret;

    @ConfigProperty(name = "srs-fleet-manager.tenant-manager.auth.realm")
    String tenantManagerAuthRealm;

    @ConfigProperty(name = "srs-fleet-manager.auth.enabled")
    boolean authEnabled;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public TenantManagerClient produce() {
        if (authEnabled) {
            log.info("Using Apicurio Registry REST TenantManagerClient with authentication enabled.");
            return new RestClientTenantManagerClientImpl(new Auth(tenantManagerAuthServerUrl, tenantManagerAuthRealm, tenantManagerAuthClientId, tenantManagerAuthSecret));
        } else {
            log.info("Using Apicurio Registry REST TenantManagerClient.");
            return new RestClientTenantManagerClientImpl();
        }
    }
}
