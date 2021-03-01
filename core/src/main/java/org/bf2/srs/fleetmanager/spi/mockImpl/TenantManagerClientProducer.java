package org.bf2.srs.fleetmanager.spi.mockImpl;

import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import io.quarkus.arc.DefaultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Produces
    @DefaultBean
    @ApplicationScoped
    public TenantManagerClient produce() {
        log.info("Using Mock TenantManagerClient.");
        return new MockTenantManagerClient();
    }
}
