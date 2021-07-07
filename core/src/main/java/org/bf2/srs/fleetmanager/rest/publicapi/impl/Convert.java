package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.rest.service.model.Registry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryList;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Convert {

    public RegistryCreate convert(RegistryCreateRest data, String owner, String orgId, Long ownerId) {
        return RegistryCreate.builder()
                .name(data.getName())
                .owner(owner)
                .ownerId(ownerId)
                .description(data.getDescription())
                .orgId(orgId)
                .build();
    }

    public RegistryStatusValueRest convert(RegistryStatusValue data) {
        switch (data) {
            case ACCEPTED:
                return RegistryStatusValueRest.accepted;
            case PROVISIONING:
                return RegistryStatusValueRest.provisioning;
            case READY:
                return RegistryStatusValueRest.ready;
            case FAILED:
                return RegistryStatusValueRest.failed;
            case REQUESTED_DEPROVISIONING:
                return RegistryStatusValueRest.deprovision;
            case DEPROVISIONING_DELETING:
                return RegistryStatusValueRest.deleting;
        }
        throw new IllegalStateException("Unreachable.");
    }

    public RegistryRest convert(Registry data) {
        RegistryRest res = new RegistryRest();
        res.setId(data.getId());
        res.setKind(data.getKind());
        res.setHref("");
        res.setRegistryUrl(data.getRegistryUrl());
        res.setName(data.getName());
        res.setRegistryDeploymentId(Optional.ofNullable(data.getRegistryDeploymentId())
                .map(Long::intValue).orElse(null)); // TODO Conversion
        res.setStatus(convert(data.getStatus()));
        res.setOwner(data.getOwner());
        res.setCreatedAt(convert(data.getCreatedAt()));
        res.setUpdatedAt(convert(data.getUpdatedAt()));
        res.setDescription(data.getDescription());
        return res;
    }

    public RegistryListRest convert(RegistryList registries) {
        RegistryListRest res = new RegistryListRest();
        res.setKind(registries.getKind());
        res.setPage(registries.getPage());
        res.setSize(registries.getSize());
        res.setTotal(Optional.ofNullable(registries.getTotal())
                .map(Long::intValue).orElse(null)); // TODO Conversion
        res.setItems(registries.getItems().stream().map(this::convert).collect(Collectors.toList()));
        return res;
    }

    public Date convert(Instant data) {
        return Date.from(data);
    }
}
