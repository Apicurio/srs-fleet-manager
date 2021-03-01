package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.REGISTRY_HEARTBEAT_W;
import static org.bf2.srs.fleetmanager.rest.model.RegistryStatusValueRest.AVAILABLE;
import static org.bf2.srs.fleetmanager.rest.model.RegistryStatusValueRest.UNAVAILABLE;

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
            registry.getStatus().setValue(UNAVAILABLE.value());
            // TODO alerting?
            log.warn("Registry with ID {} has become unreachable.", registry.getId());
        } else {
            registry.getStatus().setValue(AVAILABLE.value());
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
