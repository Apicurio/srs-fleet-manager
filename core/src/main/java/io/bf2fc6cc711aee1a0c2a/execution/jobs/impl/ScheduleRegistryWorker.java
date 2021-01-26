package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.ProvisionRegistryTenantTask;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.ScheduleRegistryTask;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.SCHEDULE_REGISTRY;

@ApplicationScoped
public class ScheduleRegistryWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == SCHEDULE_REGISTRY;
    }

    @Transactional
    @Override
    public void execute(Task aTask) {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        Optional<Registry> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            throw new IllegalStateException("Registry not found.");
        }
        Registry registry = registryOptional.get();

        List<RegistryDeployment> registryDeployments = storage.getAllRegistryDeployments();
        if (registryDeployments.size() == 0) {
            throw new IllegalStateException("No registry deployments available for scheduling.");
        }
        // Schedule to a random registry deployment
        // TODO Improve & use a scheduling strategy

        RegistryDeployment registryDeployment = registryDeployments.get(ThreadLocalRandom.current().nextInt(registryDeployments.size()));

        log.info("Scheduling {} to {}.", registry, registryDeployment);

        registry.setRegistryDeployment(registryDeployment);
        registry.getStatus().setLastUpdated(Instant.now());

        storage.createOrUpdateRegistry(registry);

        tasks.submit(ProvisionRegistryTenantTask.builder().registryId(registry.getId()).build());
    }
}
