package io.bf2fc6cc711aee1a0c2a.rest.convert;

import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryCreateRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryRest;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistry {

    @Inject
    ConvertRegistryStatus convertRegistryStatus;

    public RegistryRest convert(@Valid Registry registry) { // TODO @NotNull ?
        requireNonNull(registry);
        return RegistryRest.builder()
                .id(registry.getId())
                .name(registry.getName())
                .registryUrl(registry.getRegistryUrl())
                .status(convertRegistryStatus.convert(registry.getStatus()))
                .registryDeploymentId(ofNullable(registry.getRegistryDeployment()).map(RegistryDeployment::getId).orElse(null))
                .build();
    }

    public Registry convert(@Valid RegistryCreateRest registryCreate) {
        requireNonNull(registryCreate);
        return Registry.builder()
                .name(registryCreate.getName())
                .status(convertRegistryStatus.initial())
                .build();
    }
}
