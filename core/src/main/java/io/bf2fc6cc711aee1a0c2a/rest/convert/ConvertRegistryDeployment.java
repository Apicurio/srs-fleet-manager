package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentCreateRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

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
