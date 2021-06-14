package org.bf2.srs.fleetmanager.rest.impl;

import org.bf2.srs.fleetmanager.rest.RegistryDeploymentsResourceV1;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import org.bf2.srs.fleetmanager.rest.convert.ConvertRegistryDeployment;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentsResourceV1Impl implements RegistryDeploymentsResourceV1 {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistryDeployment convertRegistryDeployment;

    @Override
    public Response createRegistryDeployment(@Valid RegistryDeploymentCreateRest deploymentCreate) throws StorageConflictException {
        //TODO validate values
        //registryDeploymentURl finishes without / starts with http ...
        RegistryDeployment deployment = convertRegistryDeployment.convert(deploymentCreate);
        storage.createOrUpdateRegistryDeployment(deployment);
        tasks.submit(RegistryDeploymentHeartbeatTask.builder().deploymentId(deployment.getId()).build());
        return Response.accepted(convertRegistryDeployment.convert(deployment)).build();
    }

    @Override
    public List<RegistryDeploymentRest> getRegistryDeployments() {
        return storage.getAllRegistryDeployments().stream()
                .map(convertRegistryDeployment::convert)
                .collect(toList());
    }

    @Override
    public RegistryDeploymentRest getRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        return storage.getRegistryDeploymentById(id)
                .map(convertRegistryDeployment::convert)
                .orElseThrow(() -> RegistryDeploymentNotFoundException.create(id));
    }

    @Override
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException, StorageConflictException {
        storage.deleteRegistryDeployment(id);
    }
}
