package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.RegistryDeploymentHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT;

@ApplicationScoped
public class RegistryDeploymentHeartbeatWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == REGISTRY_DEPLOYMENT_HEARTBEAT;
    }

    @Transactional
    @Override
    public void execute(Task aTask) {
        RegistryDeploymentHeartbeatTask task = (RegistryDeploymentHeartbeatTask) aTask;

        Optional<RegistryDeployment> deploymentOptional = storage.getRegistryDeploymentById(task.getDeploymentId());
        if (deploymentOptional.isEmpty()) {
            // remove this task?
            // TODO Use a separate task for removing registries & deployments
            log.warn("Registry Deployment not found. Removing task.");
            tasks.remove(task);
        }
        RegistryDeployment deployment = deploymentOptional.get();

        TenantManager tenantManager = TenantManager.builder()
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .registryDeploymentUrl(deployment.getRegistryDeploymentUrl())
                .build();

        boolean ok = tmClient.pingTenantManager(tenantManager);

        deployment.getStatus().setLastUpdated(Instant.now());

        if (!ok) {
            deployment.getStatus().setStatus("UNAVAILABLE");
            // TODO ... ?
        } else {
            deployment.getStatus().setStatus("AVAILABLE");
        }

        storage.createOrUpdateRegistryDeployment(deployment);
    }
}
