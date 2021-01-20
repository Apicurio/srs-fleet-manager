package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentStatusRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeploymentStatus;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConvertRegistryDeploymentStatus {

    @Inject
    ConvertISO8601 convertISO8601;

    @SneakyThrows
    public RegistryDeploymentStatusRest convert(RegistryDeploymentStatus status) {
        return RegistryDeploymentStatusRest.builder()
                .status(status.getStatus())
                .lastUpdated(convertISO8601.convert(status.getLastUpdated()))
                .build();
    }
}
