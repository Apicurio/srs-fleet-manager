package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.Utils;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException, RegistryNotFoundException, AccountManagementServiceException, TenantManagerServiceException {
        var task = (DeprovisionRegistryTask) aTask;
        var registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();
            RegistryDeploymentData registryDeployment = registry.getRegistryDeployment();

            // FAILURE POINT 2
            if (task.getRegistryTenantId() == null) {
                final var tenantId = registry.getId();
                TenantManagerConfig tenantManagerConfig = Utils.createTenantManagerConfig(registryDeployment);
                try {
                    tms.deleteTenant(tenantManagerConfig, tenantId);
                    log.debug("Tenant id='{}' delete request send.", tenantId);
                } catch (TenantNotFoundServiceException ex) {
                    log.info("Tenant id='{}' does not exist (already deleted?).", tenantId);
                }
                task.setRegistryTenantId(tenantId);
            }

            /* Considerations for eval instances:
             * - No need for differentiating when returning subscriptions
             * - Current amount of trial instances is determined directly from the database, so no need to intervene
             */

            /* Return AMS entitlement
             * FAILURE POINT 3
             * Recovery: We recover by setting the registry status to failed so we don't lose information
             *   and the process can be initiated again.
             * Reentrancy: If the registry was already returned, (i.e. failed in #3)
             *   we need to continue without raising an error, otherwise we will keep retrying.
             */
            if (!task.isAmsSuccess()) {
                final String subscriptionId = registry.getSubscriptionId();
                // TODO Workaround: Remove this once we have RHOSRTrial working.
                if (subscriptionId != null && RegistryInstanceTypeValueDto.of(registry.getInstanceType()) != RegistryInstanceTypeValueDto.EVAL) {
                    try {
                        ams.deleteSubscription(subscriptionId);
                    } catch (SubscriptionNotFoundServiceException ex) {
                        log.info("Subscription ID '{}' for tenant ID '{}' does not exist (already deleted?).", subscriptionId, task.getRegistryTenantId());
                    }
                } else {
                    log.debug("Deleting an eval instance {} without calling AMS.", registry.getId());
                }
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
            log.debug("Registry id='{}' not found. Stopping.", task.getRegistryId());
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
