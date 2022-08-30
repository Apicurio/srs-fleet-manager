package org.bf2.srs.fleetmanager.service.deployment;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 * Load initial deployments for local development,
 * when also running Registry and Tenant Manager with default configuration.
 *
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class LocalDeploymentProvider implements DeploymentProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public List<RegistryDeploymentCreate> getDeploymentsToLoad() {

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
