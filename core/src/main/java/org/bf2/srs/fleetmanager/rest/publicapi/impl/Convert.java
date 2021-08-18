package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryListDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Convert {

    public RegistryCreateDto convert(RegistryCreate data) {
        return RegistryCreateDto.builder()
                .name(data.getName())
                .description(data.getDescription())
                .build();
    }

    public RegistryStatusValue convert(RegistryStatusValueDto data) {
        switch (data) {
            case ACCEPTED:
                return RegistryStatusValue.accepted;
            case PROVISIONING:
                return RegistryStatusValue.provisioning;
            case READY:
                return RegistryStatusValue.ready;
            case FAILED:
                return RegistryStatusValue.failed;
            case REQUESTED_DEPROVISIONING:
                return RegistryStatusValue.deprovision;
            case DEPROVISIONING_DELETING:
                return RegistryStatusValue.deleting;
            default:
                throw new IllegalStateException("Unexpected value: " + data);
        }
    }

    public Registry convert(RegistryDto data) {
        Registry res = new Registry();
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

    public RegistryList convert(RegistryListDto registries) {
        RegistryList res = new RegistryList();
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
