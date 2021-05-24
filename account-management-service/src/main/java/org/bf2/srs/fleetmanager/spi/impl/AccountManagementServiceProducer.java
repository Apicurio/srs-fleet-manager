package org.bf2.srs.fleetmanager.spi.impl;

import io.quarkus.arc.profile.UnlessBuildProfile;
import org.b2f.ams.client.AccountManagementSystemRestClient;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class AccountManagementServiceProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    AccountManagementSystemRestClient accountManagementSystemRestClient;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementService produce() {
        log.info("Using Apicurio Registry REST AccountManagerClient.");
        return new AccountManagementServiceImpl(accountManagementSystemRestClient);
    }
}
