package io.bf2fc6cc711aee1a0c2a.execution.impl.workers;

import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.RegistryHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.manager.WorkerContext;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryStatusValueRest;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;
import static io.bf2fc6cc711aee1a0c2a.execution.impl.workers.WorkerType.REGISTRY_HEARTBEAT_W;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryHeartbeatWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    public RegistryHeartbeatWorker() {
        super(REGISTRY_HEARTBEAT_W);
    }

    @Override
    public boolean supports(Task task) {
        return REGISTRY_HEARTBEAT_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {
        RegistryHeartbeatTask task = (RegistryHeartbeatTask) aTask;

        Optional<Registry> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            // The Registry disappeared. Just retry.
            ctl.retry();
        }
        Registry registry = registryOptional.get();

        TenantManager tenantManager = TenantManager.builder()
                .tenantManagerUrl(registry.getRegistryDeployment().getTenantManagerUrl())
                .registryDeploymentUrl(registry.getRegistryDeployment().getRegistryDeploymentUrl())
                .build();

        boolean ok = tmClient.pingTenant(tenantManager, registry.getTenantId());

        registry.getStatus().setLastUpdated(Instant.now());

        if (!ok) {
            registry.getStatus().setValue(RegistryStatusValueRest.UNAVAILABLE.value());
            // TODO alerting?
            log.warn("Registry with ID {} has become unreachable.", registry.getId());
        } else {
            registry.getStatus().setValue(RegistryStatusValueRest.AVAILABLE.value());
        }

        // NOTE: Failure point 2
        storage.createOrUpdateRegistry(registry);
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        // The Registry was deleted or storage failed.
        // We should make sure the customers won't lose data so we'll just ignore this error.
    }
}
