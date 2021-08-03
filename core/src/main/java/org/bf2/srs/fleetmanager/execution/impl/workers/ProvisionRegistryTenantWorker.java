package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.spi.model.TenantRequest;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.PROVISION_REGISTRY_TENANT_W;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ProvisionRegistryTenantWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    @Inject
    QuotaPlansService plansService;

    public ProvisionRegistryTenantWorker() {
        super(PROVISION_REGISTRY_TENANT_W);
    }

    @Override
    public boolean supports(Task task) {
        return PROVISION_REGISTRY_TENANT_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {
        // TODO Split along failure points?
        ProvisionRegistryTenantTask task = (ProvisionRegistryTenantTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());
        // NOTE: Failure point 1
        if (registryOptional.isEmpty()) {
            ctl.retry();
        }
        RegistryData registry = registryOptional.get();

        RegistryDeploymentData registryDeployment = registry.getRegistryDeployment();
        // NOTE: Failure point 2
        if (registryDeployment == null) {
            // Either the schedule task didn't run yet, or we are in trouble
            ctl.retry();
        }

        // Avoid accidentally creating orphan tenants
        if (task.getRegistryTenantId() == null) {
            registry.setTenantId(UUID.randomUUID().toString());
        } else {
            registry.setTenantId(task.getRegistryTenantId());
        }

        String registryUrl = registryDeployment.getRegistryDeploymentUrl();
        if (!registryUrl.endsWith("/")) {
            registryUrl += "/";
        }
        registryUrl += "t/" + registry.getTenantId();
        registry.setRegistryUrl(registryUrl);

        // Avoid accidentally creating orphan tenants
        if (task.getRegistryTenantId() == null) {

            TenantRequest tenantRequest = TenantRequest.builder()
                    .tenantId(registry.getTenantId())
                    .createdBy(registry.getOwner())
                    .organizationId(registry.getOrgId())
                    .build();

            TenantManager tenantManager = createTenantManager(registryDeployment);

            // NOTE: Failure point 4
            tmClient.createTenant(tenantManager, tenantRequest);

            task.setRegistryTenantId(registry.getTenantId());
        }

        // NOTE: Failure point 5
        storage.createOrUpdateRegistry(registry);

        // Update status to available in the heartbeat task, which should run ASAP
        ctl.delay(() -> tasks.submit(RegistryHeartbeatTask.builder().registryId(registry.getId()).build()));
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, StorageConflictException {

        ProvisionRegistryTenantTask task = (ProvisionRegistryTenantTask) aTask;

        RegistryData registry = storage.getRegistryById(task.getRegistryId()).orElse(null);

        RegistryDeploymentData registryDeployment = null;
        if (registry != null)
            registryDeployment = registry.getRegistryDeployment();

        // SUCCESS STATE
        if (registry != null && registry.getTenantId() != null)
            return;

        // Handle failures in "reverse" order

        // Cleanup orphan tenant
        if (registry != null && registryDeployment != null && task.getRegistryTenantId() != null) {
            tmClient.deleteTenant(createTenantManager(registryDeployment), registry.getTenantId());
        }

        // Remove registry entity
        if (registry != null) {
            storage.deleteRegistry(registry.getId());
        }
    }

    private TenantManager createTenantManager(RegistryDeploymentData registryDeployment) {
        return TenantManager.builder()
                .tenantManagerUrl(registryDeployment.getTenantManagerUrl())
                .registryDeploymentUrl(registryDeployment.getRegistryDeploymentUrl())
                .build();
    }
}
