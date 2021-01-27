package io.bf2fc6cc711aee1a0c2a.spi.impl;

import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Produces
    @ApplicationScoped
    public TenantManagerClient produce() {
        log.info("Using Apicurio Registry REST TenantManagerClient.");
        return new RestClientTenantManagerClientImpl();
    }
}
