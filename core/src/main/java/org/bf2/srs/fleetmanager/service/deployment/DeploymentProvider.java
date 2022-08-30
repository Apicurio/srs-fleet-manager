package org.bf2.srs.fleetmanager.service.deployment;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;

import java.util.List;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public interface DeploymentProvider {

    List<RegistryDeploymentCreate> getDeploymentsToLoad();
}
