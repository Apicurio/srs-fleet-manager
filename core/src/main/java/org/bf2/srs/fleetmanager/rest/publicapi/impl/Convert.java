package org.bf2.srs.fleetmanager.rest.publicapi.impl;

import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryInstanceTypeValue;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ServiceStatus;
import org.bf2.srs.fleetmanager.rest.service.model.ErrorDto;
import org.bf2.srs.fleetmanager.rest.service.model.ErrorListDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryListDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.ServiceStatusDto;

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

    public RegistryInstanceTypeValue convert(RegistryInstanceTypeValueDto data) {
        return RegistryInstanceTypeValue.fromValue(data.value());
    }

    public Registry convert(RegistryDto data) {
        Registry res = new Registry();
        res.setId(data.getId());
        res.setKind(data.getKind());
        res.setHref(data.getHref());
        res.setRegistryUrl(data.getRegistryUrl());
        res.setName(data.getName());
        res.setRegistryDeploymentId(Optional.ofNullable(data.getRegistryDeploymentId())
                .map(Long::intValue).orElse(null)); // TODO Conversion
        res.setStatus(convert(data.getStatus()));
        res.setOwner(data.getOwner());
        res.setCreatedAt(convert(data.getCreatedAt()));
        res.setUpdatedAt(convert(data.getUpdatedAt()));
        res.setDescription(data.getDescription());
        res.setInstanceType(convert(data.getInstanceType()));
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

    public Error convert(ErrorDto data) {
        var res = new Error();
        res.setId(data.getId());
        res.setKind(data.getKind());
        res.setHref(data.getHref());
        res.setCode(data.getCode());
        res.setReason(data.getReason());
        res.setOperationId(data.getOperationId());
        return res;
    }

    public ErrorList convert(ErrorListDto data) {
        var res = new ErrorList();
        res.setKind(data.getKind());
        res.setPage(data.getPage());
        res.setSize(data.getSize());
        res.setTotal(data.getTotal().intValue()); // TODO Conversion
        res.setItems(data.getItems().stream().map(this::convert).collect(Collectors.toList()));
        return res;
    }

    public ServiceStatus convert(ServiceStatusDto status) {
        ServiceStatus res = new ServiceStatus();
        res.setMaxEvalInstancesReached(status.isMaxEvalInstancesReached());
        return res;
    }
}
