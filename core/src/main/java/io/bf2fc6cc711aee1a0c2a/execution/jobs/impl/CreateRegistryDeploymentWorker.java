package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.CreateRegistryDeploymentTask;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.RegistryDeploymentHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeploymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class CreateRegistryDeploymentWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == TaskType.CREATE_REGISTRY_DEPLOYMENT;
    }

    @Transactional
    @Override
    public void execute(Task aTask) {
        CreateRegistryDeploymentTask task = (CreateRegistryDeploymentTask) aTask;

        RegistryDeploymentStatus status = RegistryDeploymentStatus.builder()
                .status("PROCESSING")
                .lastUpdated(Instant.now())
                .build();

        RegistryDeployment deployment = RegistryDeployment.builder()
                .status(status)
                .tenantManagerUrl(task.getRegistryDeployment().getTenantManagerUrl())
                .build();

        storage.createOrUpdateRegistryDeployment(deployment);

        tasks.submit(RegistryDeploymentHeartbeatTask.builder().deploymentId(deployment.getId()).build());
    }
}
