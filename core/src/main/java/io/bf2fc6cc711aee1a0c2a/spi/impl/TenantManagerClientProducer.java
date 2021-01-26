package io.bf2fc6cc711aee1a0c2a.spi.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.mockImpl.MockTenantManagerClient;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;

@Dependent
public class TenantManagerClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Produces
    @IfBuildProfile("prod")
    @ApplicationScoped
    public TenantManagerClient realClient() {
        return new RestClientTenantManagerClientImpl();
    }

    @Produces
    @DefaultBean
    @ApplicationScoped
    public TenantManagerClient mockClient() {
        log.info("Using Mocked TenantManagerClient");
        return new MockTenantManagerClient();
    }

}
