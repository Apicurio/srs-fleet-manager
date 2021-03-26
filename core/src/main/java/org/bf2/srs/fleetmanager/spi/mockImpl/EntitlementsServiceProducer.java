package org.bf2.srs.fleetmanager.spi.mockImpl;

import io.quarkus.arc.DefaultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class EntitlementsServiceProducer {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Produces
    @DefaultBean
    @ApplicationScoped
    public MockEntitlementsService produce() {
        log.info("Using Mock EntitlementsService.");
        return new MockEntitlementsService();
    }
}
