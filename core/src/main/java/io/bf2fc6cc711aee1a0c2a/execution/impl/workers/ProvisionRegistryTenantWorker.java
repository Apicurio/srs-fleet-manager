package io.bf2fc6cc711aee1a0c2a.execution.impl.workers;

import io.bf2fc6cc711aee1a0c2a.auth.AuthResource;
import io.bf2fc6cc711aee1a0c2a.auth.AuthService;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.ProvisionRegistryTenantTask;
import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.RegistryHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.manager.WorkerContext;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantRequest;
import io.bf2fc6cc711aee1a0c2a.storage.RegistryNotFoundException;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;
import static io.bf2fc6cc711aee1a0c2a.execution.impl.workers.WorkerType.PROVISION_REGISTRY_TENANT_W;

/**
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
    AuthService authService;

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

        Optional<Registry> registryOptional = storage.getRegistryById(task.getRegistryId());
        // NOTE: Failure point 1
        if (registryOptional.isEmpty()) {
            ctl.retry();
        }
        Registry registry = registryOptional.get();

        RegistryDeployment registryDeployment = registry.getRegistryDeployment();
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

        registry.setRegistryUrl(registryDeployment.getRegistryDeploymentUrl() + "/t/" + registry.getTenantId());

        // NOTE: Failure point 3
        final AuthResource authResource = authService.createTenantAuthResources(registry.getId().toString(), registry.getRegistryUrl());

        // Avoid accidentally creating orphan tenants
        if (task.getRegistryTenantId() == null) {

            TenantRequest tenantRequest = TenantRequest.builder()
                    .tenantId(registry.getTenantId())
                    .authServerUrl(authResource.getServerUrl())
                    .authClientId(authResource.getClientId())
                    .build();

            TenantManager tenantManager = createTenantManager(registryDeployment);

            // NOTE: Failure point 4
            tmClient.createTenant(tenantManager, tenantRequest);

            task.setRegistryTenantId(registry.getTenantId());
        }
        registry.getStatus().setLastUpdated(Instant.now());

        // NOTE: Failure point 5
        storage.createOrUpdateRegistry(registry);

        // Update status to available in the heartbeat task, which should run ASAP
        ctl.delay(() -> tasks.submit(RegistryHeartbeatTask.builder().registryId(registry.getId()).build()));
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException {

        ProvisionRegistryTenantTask task = (ProvisionRegistryTenantTask) aTask;

        Registry registry = storage.getRegistryById(task.getRegistryId()).orElse(null);

        RegistryDeployment registryDeployment = null;
        if (registry != null)
            registryDeployment = registry.getRegistryDeployment();

        // SUCCESS STATE
        if (registry != null && registry.getTenantId() != null)
            return;

        // Handle failures in "reverse" order

        // Cleanup orphan tenant
        if (registry != null && registryDeployment != null && task.getRegistryTenantId() != null) {
            tmClient.deleteTenant(createTenantManager(registryDeployment), registry.getTenantId());
            authService.deleteResources(registry.getId().toString());
        }

        // Remove registry entity
        if (registry != null) {
            storage.deleteRegistry(registry.getId());
        }
    }

    private TenantManager createTenantManager(RegistryDeployment registryDeployment) {
        return TenantManager.builder()
                .tenantManagerUrl(registryDeployment.getTenantManagerUrl())
                .registryDeploymentUrl(registryDeployment.getRegistryDeploymentUrl())
                .build();
    }
}
