package org.bf2.srs.fleetmanager.rest.convert;

import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentRest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistryDeployment {

    @Inject
    ConvertRegistryDeploymentStatus convertRegistryDeploymentStatus;

    public RegistryDeploymentRest convert(@Valid RegistryDeployment deployment) {
        requireNonNull(deployment);
        return RegistryDeploymentRest.builder()
                .id(deployment.getId())
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .registryDeploymentUrl(deployment.getRegistryDeploymentUrl())
                .status(convertRegistryDeploymentStatus.convert(deployment.getStatus()))
                .build();
    }

    public RegistryDeployment convert(@Valid RegistryDeploymentCreateRest deploymentCreate) {
        requireNonNull(deploymentCreate);
        return RegistryDeployment.builder()
                .tenantManagerUrl(deploymentCreate.getTenantManagerUrl())
                .registryDeploymentUrl(deploymentCreate.getRegistryDeploymentUrl())
                .name(deploymentCreate.getName())
                .status(convertRegistryDeploymentStatus.initial())
                .build();
    }
}
