package org.bf2.srs.fleetmanager.spi.impl;

import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public TenantManagerClient produce() {
        log.info("Using Apicurio Registry REST TenantManagerClient.");
        return new RestClientTenantManagerClientImpl();
    }
}
