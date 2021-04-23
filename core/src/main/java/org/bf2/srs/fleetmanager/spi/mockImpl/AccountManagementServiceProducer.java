package org.bf2.srs.fleetmanager.spi.mockImpl;

import io.quarkus.arc.DefaultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AccountManagementServiceProducer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Produces
    @DefaultBean
    @ApplicationScoped
    public MockAccountManagementService produce() {
        log.info("Using Mock Account Management Service.");
        return new MockAccountManagementService();
    }
}
