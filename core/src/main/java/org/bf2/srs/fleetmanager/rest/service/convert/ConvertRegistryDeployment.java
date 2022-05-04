package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistryDeployment {

    @Inject
    ConvertRegistryDeploymentStatus convertRegistryDeploymentStatus;

    public RegistryDeployment convert(@Valid @NotNull RegistryDeploymentData deployment) {
        return RegistryDeployment.builder()
                .id(deployment.getId())
                .name(deployment.getName())
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .registryDeploymentUrl(deployment.getRegistryDeploymentUrl())
                .status(convertRegistryDeploymentStatus.convert(deployment.getStatus()))
                .build();
    }

    public RegistryDeploymentData convert(@Valid @NotNull RegistryDeploymentCreate deploymentCreate) {
        return RegistryDeploymentData.builder()
                .tenantManagerUrl(deploymentCreate.getTenantManagerUrl())
                .registryDeploymentUrl(deploymentCreate.getRegistryDeploymentUrl())
                .name(deploymentCreate.getName())
                .status(convertRegistryDeploymentStatus.initial())
                .build();
    }
}
