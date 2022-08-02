package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;

import java.util.List;

/**
 * Notice: The loaders are not executed in any specific order.
 * If there are deployments with the same name, the import will fail.
 *
 * @author Jakub Senko <m@jsenko.net>
 */
public interface DeploymentLoader {

    List<RegistryDeploymentCreate> getDeploymentsToLoad();
}
