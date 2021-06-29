package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

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

    public Registry convert(@Valid RegistryData registry) { // TODO @NotNull ?
        requireNonNull(registry);
        return Registry.builder()
                .id(registry.getId().toString())
                .name(registry.getName())
                .registryUrl(registry.getRegistryUrl())
                .status(RegistryStatusValue.valueOf(registry.getStatus().getValue()))
                .owner(registry.getOwner())
                .registryDeploymentId(ofNullable(registry.getRegistryDeployment()).map(RegistryDeploymentData::getId).orElse(null))
                .build();
    }

    public RegistryData convert(@Valid RegistryCreate registryCreate) {
        requireNonNull(registryCreate);
        return RegistryData.builder()
                .name(registryCreate.getName())
                .status(convertRegistryStatus.initial())
                .owner(registryCreate.getOwner())
                .build();
    }
}
