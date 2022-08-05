package org.bf2.srs.fleetmanager.rest.service.convert;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;

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

    public RegistryDto convert(@Valid @NotNull RegistryData registry) {
        return RegistryDto.builder()
                .id(registry.getId())
                .name(registry.getName())
                .registryUrl(registry.getRegistryUrl())
                .owner(registry.getOwner())
                .orgId(registry.getOrgId())
                .status(RegistryStatusValueDto.of(registry.getStatus()))
                .registryDeploymentId(ofNullable(registry.getRegistryDeployment()).map(RegistryDeploymentData::getId).orElse(null))
                .createdAt(registry.getCreatedAt())
                .updatedAt(registry.getUpdatedAt())
                .description(registry.getDescription())
                .instanceType(RegistryInstanceTypeValueDto.of(registry.getInstanceType()))
                .build();
    }

    public RegistryData convert(@Valid @NotNull RegistryCreateDto registryCreate,
                                String subscriptionId, String owner, String orgId, Long ownerId, RegistryInstanceTypeValueDto instanceType) {
        requireNonNull(registryCreate);
        return RegistryData.builder()
                .name(registryCreate.getName())
                .description(registryCreate.getDescription())
                .owner(owner)
                .ownerId(ownerId)
                .orgId(orgId)
                .status(RegistryStatusValueDto.ACCEPTED.value())
                .subscriptionId(subscriptionId)
                .instanceType(instanceType.value())
                .build();
    }
}
