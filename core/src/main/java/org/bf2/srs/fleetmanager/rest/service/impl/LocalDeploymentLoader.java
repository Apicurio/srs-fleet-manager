package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 * Load initial deployments for local development,
 * when also running Registry and Tenant Manager with default configuration.
 *
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class LocalDeploymentLoader implements DeploymentLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.deployments.local.enable")
    boolean enableLocalDeployments;

    @Override
    public List<RegistryDeploymentCreate> getDeploymentsToLoad() {

        if (!enableLocalDeployments) {
            return Collections.emptyList();
        }

        log.info("Loading default deployment for local development");

        return List.of(
            RegistryDeploymentCreate.builder()
                    .name("local")
                    .tenantManagerUrl("http://localhost:8585")
                    .registryDeploymentUrl("http://localhost:8080")
                    .build()
        );
    }
}
