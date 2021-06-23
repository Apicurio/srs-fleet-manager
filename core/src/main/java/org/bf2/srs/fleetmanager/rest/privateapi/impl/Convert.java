package org.bf2.srs.fleetmanager.rest.privateapi.impl;

import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentStatusRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentStatusValueRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.TaskRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.TaskScheduleRest;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatus;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.Task;
import org.bf2.srs.fleetmanager.rest.service.model.TaskSchedule;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Convert {

    public TaskScheduleRest convert(TaskSchedule data) {
        TaskScheduleRest res = new TaskScheduleRest();
        res.setFirstExecuteAt(data.getFirstExecuteAt());
        res.setIntervalSec(Optional.ofNullable(data.getIntervalSec())
                .map(Long::intValue).orElse(null)); // TODO Conversion
        res.setPriority(data.getPriority());
        return res;
    }

    public TaskRest convert(Task data) {
        TaskRest res = new TaskRest();
        res.setId(data.getId());
        res.setData(data.getData());
        res.setSchedule(convert(data.getSchedule()));
        res.setType(data.getType());
        return res;
    }

    public RegistryDeploymentRest convert(RegistryDeployment data) {
        RegistryDeploymentRest res = new RegistryDeploymentRest();
        res.setId(Optional.ofNullable(data.getId())
                .map(Long::intValue).orElse(null)); // TODO Conversion
        res.setName(data.getName());
        res.setStatus(convert(data.getStatus()));
        res.setRegistryDeploymentUrl(data.getRegistryDeploymentUrl());
        res.setTenantManagerUrl(data.getTenantManagerUrl());
        return res;
    }

    private RegistryDeploymentStatusRest convert(RegistryDeploymentStatus data) {
        RegistryDeploymentStatusRest res = new RegistryDeploymentStatusRest();
        res.setValue(convert(data.getValue()));
        res.setLastUpdated(Optional.ofNullable(data.getLastUpdated())
                .map(x -> Date.from(Instant.parse(x))).orElse(null)); // TODO Conversion
        return res;
    }

    private RegistryDeploymentStatusValueRest convert(RegistryDeploymentStatusValue data) {
        switch (data) {
            case PROCESSING:
                return RegistryDeploymentStatusValueRest.PROCESSING;
            case AVAILABLE:
                return RegistryDeploymentStatusValueRest.AVAILABLE;
            case UNAVAILABLE:
                return RegistryDeploymentStatusValueRest.UNAVAILABLE;
        }
        throw new IllegalStateException("Unreachable.");
    }

    public RegistryDeploymentCreate convert(RegistryDeploymentCreateRest data) {
        return RegistryDeploymentCreate.builder()
                .name(data.getName())
                .registryDeploymentUrl(data.getRegistryDeploymentUrl())
                .tenantManagerUrl(data.getTenantManagerUrl())
                .build();
    }

}
