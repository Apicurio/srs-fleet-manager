package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.RegistryHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.Tenant;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.REGISTRY_HEARTBEAT;

@ApplicationScoped
public class RegistryHeartbeatWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == REGISTRY_HEARTBEAT;
    }

    @Transactional
    @Override
    public void execute(Task task) {
        RegistryHeartbeatTask thisTask = (RegistryHeartbeatTask) task;

        Optional<Registry> registryOptional = storage.getRegistryById(thisTask.getRegistryId());
        if (registryOptional.isEmpty()) {
            // remove this task?
            // TODO Use a separate task for removing registries
            log.warn("Registry not found. Removing task.");
            tasks.remove(thisTask);
        }
        Registry registry = registryOptional.get();

        TenantManager tenantManager = TenantManager.builder().tenantManagerUrl(registry.getRegistryDeployment().getTenantManagerUrl()).build();
        Tenant tenant = Tenant.builder().tenantApiUrl(registry.getAppUrl())
                .id(registry.getAppUrl().substring(registry.getAppUrl().lastIndexOf("/") + 1)) // TODO This is a temporary hack:(
                .build();

        boolean ok = tmClient.pingTenant(tenantManager, tenant);

        registry.getStatus().setLastUpdated(Instant.now());

        if (!ok) {
            registry.getStatus().setStatus("UNAVAILABLE");
            // TODO ... ?
        } else {
            registry.getStatus().setStatus("AVAILABLE");
        }

        storage.createOrUpdateRegistry(registry);
    }
}
