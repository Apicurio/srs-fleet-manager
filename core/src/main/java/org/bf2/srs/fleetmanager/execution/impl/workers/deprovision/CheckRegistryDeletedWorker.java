package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.CheckRegistryDeletedTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.Utils;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.Tenant;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.TenantStatus;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
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
public class CheckRegistryDeletedWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

    @Inject
    AccountManagementService ams;

    public CheckRegistryDeletedWorker() {
        super(WorkerType.CHECK_REGISTRY_DELETED_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.CHECK_REGISTRY_DELETED_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException, RegistryNotFoundException {

        CheckRegistryDeletedTask task = (CheckRegistryDeletedTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();
            TenantManagerConfig tmc = Utils.createTenantManagerConfig(registry.getRegistryDeployment());
            Optional<Tenant> tenant = tmClient.getTenantById(tmc, registry.getTenantId());
            if (tenant.isPresent()) {
                if (TenantStatus.DELETED.equals(tenant.get().getStatus())) {
                    /* Return AMS entitlement
                     * FAILURE POINT 2
                     * Recovery: We recover by setting the registry status to failed so we don't lose information
                     *   and the process can be initiated again.
                     * Reentrancy: If the registry was already returned, (i.e. failed in #3)
                     *   we need to continue without raising an error, otherwise we will keep retrying.
                     */
                    if (!task.isAmsSuccess()) {
                        final String subscriptionId = registry.getSubscriptionId();
                        ams.deleteSubscription(subscriptionId);
                        task.setAmsSuccess(true);
                    }
                    /* Delete the tenant from DB
                     * FAILURE POINT 3
                     * Recovery: We set the status to failed so it can be retried.
                     * Reentrancy: This is the last step, so nothing to do.
                     */
                    storage.deleteRegistry(registry.getId());
                    // TODO Do we want to delete the tenant record from tenant manager?
                    ctl.delay(ctl::stop);
                    // TODO TRANSACTIONS
                }
            } else {
                log.warn("Tenant (ID = {}) not found. Retrying.", registry.getTenantId());
                ctl.retry();
            }
        } else {
            ctl.retry();
        }
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, StorageConflictException {

        CheckRegistryDeletedTask task = (CheckRegistryDeletedTask) aTask;

        var optionalRegistry = storage.getRegistryById(task.getRegistryId());

        // If the Registry is not present, either the task completed successfully or something else did the deletion
        if (optionalRegistry.isPresent()) {
            var registry = optionalRegistry.get();
            // Let's move the registry to failed state, and try to return the subscription
            // one more time.
            registry.setStatus(RegistryStatusValue.FAILED.value());
            storage.createOrUpdateRegistry(registry);
            if (!task.isAmsSuccess()) {
                final String subscriptionId = registry.getSubscriptionId();
                ams.deleteSubscription(subscriptionId);
            }
        }
    }
}
