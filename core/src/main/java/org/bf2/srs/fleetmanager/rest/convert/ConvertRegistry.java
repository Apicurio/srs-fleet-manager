package org.bf2.srs.fleetmanager.rest.convert;

import org.bf2.srs.fleetmanager.rest.model.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.model.RegistryRest;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment;

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
                .id(registry.getId().toString())
                .name(registry.getName())
                .registryUrl(registry.getRegistryUrl())
                .status(registry.getStatus().getValue())
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
