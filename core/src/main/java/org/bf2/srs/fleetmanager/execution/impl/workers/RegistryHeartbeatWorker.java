package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.REGISTRY_HEARTBEAT_W;
import static org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue.FAILED;
import static org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue.READY;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryHeartbeatWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

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

        // TODO Stop when tenant status is (deleting)?
        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            // The Registry disappeared. Just retry.
            // It could've been deprovisioned!
            ctl.retry();
        }
        RegistryData registry = registryOptional.get();

        TenantManagerConfig tenantManager = TenantManagerConfig.builder()
                .tenantManagerUrl(registry.getRegistryDeployment().getTenantManagerUrl())
                .registryDeploymentUrl(registry.getRegistryDeployment().getRegistryDeploymentUrl())
                .build();

        boolean ok = tmClient.pingTenant(tenantManager, registry.getTenantId());

        if (!ok) {
            registry.setStatus(FAILED.value());
            // TODO alerting?
            log.warn("Registry with ID {} has become unreachable.", registry.getId());
        } else {
            registry.setStatus(READY.value());
        }

        // NOTE: Failure point 2
        storage.createOrUpdateRegistry(registry);
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        // The Registry was deprovisioned, deleted or storage failed.
        // We should make sure the customers won't lose data so we'll just ignore this error.
    }
}
