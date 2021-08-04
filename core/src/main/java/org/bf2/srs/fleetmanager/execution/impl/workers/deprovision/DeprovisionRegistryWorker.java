package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.CheckRegistryDeletedTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.Utils;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class DeprovisionRegistryWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

    @Inject
    TaskManager tasks;

    public DeprovisionRegistryWorker() {
        super(WorkerType.DEPROVISION_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.DEPROVISION_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {

        DeprovisionRegistryTask task = (DeprovisionRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();

            RegistryDeploymentData registryDeployment = registry.getRegistryDeployment();

            if (task.getRegistryTenantId() == null) {
                final var tenantId = registry.getTenantId();
                TenantManagerConfig tenantManagerConfig = Utils.createTenantManagerConfig(registryDeployment);
                // FAILURE POINT 2
                tmClient.deleteTenant(tenantManagerConfig, tenantId);
                task.setRegistryTenantId(tenantId);
            }

            // FAILURE POINT 3
            registry.setStatus(RegistryStatusValue.DEPROVISIONING_DELETING.value());
            storage.createOrUpdateRegistry(registry);

            ctl.delay(() -> tasks.submit(CheckRegistryDeletedTask.builder().registryId(registry.getId()).build()));

        } else {
            ctl.retry();
        }
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, StorageConflictException {

        DeprovisionRegistryTask task = (DeprovisionRegistryTask) aTask;

        Optional<RegistryData> registry = storage.getRegistryById(task.getRegistryId());

        if (registry.isPresent()) {
            var reg = registry.get();
            // SUCCESS STATE
            if (RegistryStatusValue.DEPROVISIONING_DELETING.value().equals(reg.getStatus()))
                return;

            // Failure - Could not delete tenant or update status
            // Try updating status to failed, otherwise user can retry.
            reg.setStatus(RegistryStatusValue.FAILED.value());
            storage.createOrUpdateRegistry(reg);
            log.warn("Failed to deprovision Registry: {}", registry);
        } else {
            // Registry not found.
            // It is possible that it was deprovisioned in the meantime, so not necessarily an issue.
            log.warn("Could not find Registry (ID = {}).", task.getRegistryId());
        }
    }
}
