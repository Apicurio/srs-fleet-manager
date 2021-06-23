package org.bf2.srs.fleetmanager.rest.privateapi.impl;

import lombok.SneakyThrows;
import org.bf2.srs.fleetmanager.rest.privateapi.ApiResource;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.TaskRest;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.TaskService;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ApiResourceImpl implements ApiResource {

    private static final String SCHEMA;

    static {
        try {
            SCHEMA = new String(ApiResourceImpl.class.getResourceAsStream("/srs-fleet-manager-private.json").readAllBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load Open API schema for the v1 REST interface.");
        }
    }

    @Inject
    RegistryDeploymentService registryDeploymentService;

    @Inject
    TaskService taskService;

    @Inject
    Convert convert;

    @Override
    public List<TaskRest> getTasks() {
        return taskService.getTasks().stream().map(convert::convert).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public TaskRest getTask(String taskId) {
        return convert.convert(taskService.getTask(taskId));
    }

    @Override
    public String getSchema() {
        return SCHEMA;
    }

    @Override
    public List<RegistryDeploymentRest> getRegistryDeployments() {
        return registryDeploymentService.getRegistryDeployments().stream().map(convert::convert).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public RegistryDeploymentRest createRegistryDeployment(RegistryDeploymentCreateRest data) {
        return convert.convert(registryDeploymentService.createRegistryDeployment(convert.convert(data)));
    }

    @SneakyThrows
    @Override
    public RegistryDeploymentRest getRegistryDeployment(Integer registryDeploymentId) {
        return convert.convert(registryDeploymentService.getRegistryDeployment(registryDeploymentId.longValue())); // TODO Conversion
    }

    @SneakyThrows
    @Override
    public void deleteRegistryDeployment(Integer registryDeploymentId) {
        registryDeploymentService.deleteRegistryDeployment(registryDeploymentId.longValue()); // TODO Conversion
    }
}
