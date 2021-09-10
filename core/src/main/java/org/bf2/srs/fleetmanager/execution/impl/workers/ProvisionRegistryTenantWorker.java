package org.bf2.srs.fleetmanager.execution.impl.workers;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.PROVISION_REGISTRY_TENANT_W;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ProvisionRegistryTenantWorker extends AbstractWorker {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

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
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException {
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

        String registryUrl = registryDeployment.getRegistryDeploymentUrl();
        if (!registryUrl.endsWith("/")) {
            registryUrl += "/";
        }
        registryUrl += "t/" + registry.getId();
        registry.setRegistryUrl(registryUrl);

        // Avoid accidentally creating orphan tenants
        if (task.getRegistryTenantId() == null) {

            CreateTenantRequest tenantRequest = CreateTenantRequest.builder()
                    .tenantId(registry.getId())
                    .createdBy(registry.getOwner())
                    .organizationId(registry.getOrgId())
                    .resources(plansService.getDefaultQuotaPlan().getResources())
                    .build();

            TenantManagerConfig tenantManager = Utils.createTenantManagerConfig(registryDeployment);

            // NOTE: Failure point 4
            tmClient.createTenant(tenantManager, tenantRequest);

            task.setRegistryTenantId(registry.getId());
        }

        // NOTE: Failure point 5
        registry.setStatus(RegistryStatusValueDto.READY.value());
        storage.createOrUpdateRegistry(registry);

        // TODO This task is (temporarily) not used. Enable when needed.
        // Update status to available in the heartbeat task, which should run ASAP
        //ctl.delay(() -> tasks.submit(RegistryHeartbeatTask.builder().registryId(registry.getId()).build()));
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, RegistryStorageConflictException {

        ProvisionRegistryTenantTask task = (ProvisionRegistryTenantTask) aTask;

        RegistryData registry = storage.getRegistryById(task.getRegistryId()).orElse(null);

        RegistryDeploymentData registryDeployment = null;
        if (registry != null)
            registryDeployment = registry.getRegistryDeployment();

        // SUCCESS STATE
        if (registry != null && registry.getRegistryUrl() != null)
            return;

        // Handle failures in "reverse" order
        // TODO In case of failure, return resource to AMS!

        // Cleanup orphan tenant
        if (registry != null && registryDeployment != null && task.getRegistryTenantId() != null) {
            tmClient.deleteTenant(Utils.createTenantManagerConfig(registryDeployment), registry.getId());
        }

        // Remove registry entity
        if (registry != null) {
            storage.deleteRegistry(registry.getId());
        }
    }
}
