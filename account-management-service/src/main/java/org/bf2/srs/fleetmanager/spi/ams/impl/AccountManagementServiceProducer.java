package org.bf2.srs.fleetmanager.spi.ams.impl;

import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.RemoteAMS;
import org.bf2.srs.fleetmanager.spi.ams.impl.remote.RemoteAMSProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class AccountManagementServiceProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "fm.ams.type")
    String amsType;

    @Inject
    LocalAMSProperties localAMSProperties;

    @Inject
    RemoteAMSProperties remoteAMSProperties;

    @Inject
    ResourceStorage storage;

    // Do not annotate with @Produces!
    // This method is called by org.bf2.srs.fleetmanager.spi.ams.impl.AccountManagementServiceWrapper
    public AccountManagementService produces() {
        switch (amsType) {
            case "LOCAL": {
                log.info("Using Local Account Management Service.");
                return new LocalAMS(localAMSProperties, storage);
            }
            case "REMOTE": {
                log.info("Using Remote Account Management Service with Account Management URL: {}", remoteAMSProperties.getEndpoint());
                return new RemoteAMS(remoteAMSProperties);
            }
            default:
                throw new IllegalStateException("Account Management Service type '" + amsType + "' is not available");
        }
    }
}
