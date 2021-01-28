package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.CreateRegistryTask;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.ScheduleRegistryTask;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryStatus;

import java.time.Instant;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CreateRegistryWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == TaskType.CREATE_REGISTRY;
    }

    @Transactional
    @Override
    public void execute(Task task) {
        CreateRegistryTask createRegistryTask = (CreateRegistryTask) task;

        RegistryStatus status = RegistryStatus.builder()
                .status("PROVISIONING")
                .lastUpdated(Instant.now())
                .build();

        Registry storageRegistry = Registry.builder()
                .name(createRegistryTask.getRegistry().getName())
                .tenantId(UUID.randomUUID().toString())
                .status(status)
                .build();

        storage.createOrUpdateRegistry(storageRegistry);

        log.info("Registry {} created. id: {}", storageRegistry.getName(), storageRegistry.getId());

        tasks.submit(ScheduleRegistryTask.builder().registryId(storageRegistry.getId()).build());
    }
}
