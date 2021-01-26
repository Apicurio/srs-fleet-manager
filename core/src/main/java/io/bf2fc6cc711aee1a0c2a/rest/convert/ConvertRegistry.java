package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ConvertRegistry {

    @Inject
    ConvertRegistryStatus convertRegistryStatus;

    @SneakyThrows
    public RegistryRest convert(Registry registry) {
        return RegistryRest.builder()
                .id(registry.getId())
                .name(registry.getName())
                .appUrl(registry.getAppUrl())
                .status(convertRegistryStatus.convert(registry.getStatus()))
                .build();
    }
}
