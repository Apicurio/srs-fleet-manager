package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.Utils;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
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
    TenantManagerService tms;

    @Inject
    AccountManagementService ams;

    public DeprovisionRegistryWorker() {
        super(WorkerType.DEPROVISION_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.DEPROVISION_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException, RegistryNotFoundException {
        var task = (DeprovisionRegistryTask) aTask;
        var registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();
            RegistryDeploymentData registryDeployment = registry.getRegistryDeployment();

            // FAILURE POINT 2
            if (task.getRegistryTenantId() == null) {
                final var tenantId = registry.getId();
                TenantManagerConfig tenantManagerConfig = Utils.createTenantManagerConfig(registryDeployment);
                tms.deleteTenant(tenantManagerConfig, tenantId);
                task.setRegistryTenantId(tenantId);
                log.debug("Tenant id='{}' delete request send.", tenantId);
            }

            /* Return AMS entitlement
             * FAILURE POINT 3
             * Recovery: We recover by setting the registry status to failed so we don't lose information
             *   and the process can be initiated again.
             * Reentrancy: If the registry was already returned, (i.e. failed in #3)
             *   we need to continue without raising an error, otherwise we will keep retrying.
             */
            if (!task.isAmsSuccess()) {
                final String subscriptionId = registry.getSubscriptionId();
                ams.deleteSubscription(subscriptionId);
                task.setAmsSuccess(true);
                log.debug("Subscription (id='{}') for Registry (id='{}') deleted.", subscriptionId, registry.getId());
            }

            /* Delete the registry from DB
             * FAILURE POINT 4
             * Recovery: We set the status to failed so it can be retried.
             * Reentrancy: This is the last step, so nothing to do.
             */
            storage.deleteRegistry(registry.getId());
        } else {
            log.warn("Registry id='{}' not found. Stopping.", task.getRegistryId());
            ctl.stop();
        }
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, RegistryStorageConflictException {
        DeprovisionRegistryTask task = (DeprovisionRegistryTask) aTask;
        Optional<RegistryData> registry = storage.getRegistryById(task.getRegistryId());

        if (registry.isPresent()) {
            var reg = registry.get();
            // Failure - Could not delete tenant or update status
            // Try updating status to failed, otherwise user can retry.
            reg.setStatus(RegistryStatusValueDto.FAILED.value());
            // TODO Add failed_reason
            storage.createOrUpdateRegistry(reg);
            log.warn("Failed to deprovision Registry: {}", registry);
        } else {
            // SUCCESS
            log.debug("Registry (ID = {}) has been deleted.", task.getRegistryId());
        }
    }
}
