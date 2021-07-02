package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ConvertRegistry {

    public Registry convert(@Valid @NotNull RegistryData registry) {
        return Registry.builder()
                .id(registry.getId().toString())
                .name(registry.getName())
                .registryUrl(registry.getRegistryUrl())
                .owner(registry.getOwner())
                .orgId(registry.getOrgId())
                .status(RegistryStatusValue.fromValue(registry.getStatus()))
                .registryDeploymentId(ofNullable(registry.getRegistryDeployment()).map(RegistryDeploymentData::getId).orElse(null))
                .createdAt(registry.getCreatedAt())
                .updatedAt(registry.getUpdatedAt())
                .description(registry.getDescription())
                .build();
    }

    public RegistryData convert(@Valid @NotNull RegistryCreate registryCreate) {
        requireNonNull(registryCreate);
        return RegistryData.builder()
                .name(registryCreate.getName())
                .owner(registryCreate.getOwner())
                .orgId(registryCreate.getOrgId())
                .status(RegistryStatusValue.ACCEPTED.value())
                .build();
    }
}
