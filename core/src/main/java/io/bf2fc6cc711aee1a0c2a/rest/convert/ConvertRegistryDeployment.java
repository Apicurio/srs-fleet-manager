package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConvertRegistryDeployment {

    @Inject
    ConvertRegistryDeploymentStatus convertRegistryDeploymentStatus;

    @SneakyThrows
    public RegistryDeploymentRest convert(RegistryDeployment deployment) {
        return RegistryDeploymentRest.builder()
                .id(deployment.getId())
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .status(convertRegistryDeploymentStatus.convert(deployment.getStatus()))
                .build();
    }
}
