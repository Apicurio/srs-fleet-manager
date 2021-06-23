package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeployment;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentCreate;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import static java.util.stream.Collectors.toList;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentServiceImpl implements RegistryDeploymentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @Override
    public RegistryDeployment createRegistryDeployment(@Valid RegistryDeploymentCreate deploymentCreate) throws StorageConflictException {
        //TODO validate values
        //registryDeploymentURl finishes without / starts with http ...
        RegistryDeploymentData deployment = convertRegistryDeployment.convert(deploymentCreate);
        storage.createOrUpdateRegistryDeployment(deployment);
        tasks.submit(RegistryDeploymentHeartbeatTask.builder().deploymentId(deployment.getId()).build());
        return convertRegistryDeployment.convert(deployment);
    }

    @Override
    public List<RegistryDeployment> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    @Override
    public RegistryDeployment getRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElseThrow(() -> RegistryDeploymentNotFoundException.create(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, StorageConflictException {
        storage.deleteRegistryDeployment(id);
    }
}
