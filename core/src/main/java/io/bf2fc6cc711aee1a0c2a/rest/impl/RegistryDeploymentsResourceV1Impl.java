package io.bf2fc6cc711aee1a0c2a.rest.impl;

import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.rest.RegistryDeploymentsResourceV1;
import io.bf2fc6cc711aee1a0c2a.rest.convert.ConvertRegistryDeployment;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentCreateRest;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentRest;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryDeploymentNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
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
    public void deleteRegistryDeployment(Long id) throws RegistryDeploymentNotFoundException {
        storage.deleteRegistryDeployment(id);
    }
}
