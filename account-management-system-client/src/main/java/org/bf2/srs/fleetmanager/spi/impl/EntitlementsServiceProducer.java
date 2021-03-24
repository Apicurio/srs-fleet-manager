package org.bf2.srs.fleetmanager.spi.impl;

import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.spi.AccountManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class EntitlementsServiceProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagerClient produce() {
        log.info("Using Apicurio Registry REST AccountManagerClient.");
        return new EntitlementsService();
    }
}
