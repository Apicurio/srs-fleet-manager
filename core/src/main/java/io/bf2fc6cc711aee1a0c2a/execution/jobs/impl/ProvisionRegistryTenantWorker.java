package io.bf2fc6cc711aee1a0c2a.execution.jobs.impl;

import io.bf2fc6cc711aee1a0c2a.auth.AuthResource;
import io.bf2fc6cc711aee1a0c2a.auth.AuthService;
import io.bf2fc6cc711aee1a0c2a.execution.jobs.Worker;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.Task;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.ProvisionRegistryTenantTask;
import io.bf2fc6cc711aee1a0c2a.execution.tasks.impl.RegistryHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.Tenant;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantRequest;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.Registry;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.tasks.TaskType.PROVISION_REGISTRY_TENANT;

@ApplicationScoped
public class ProvisionRegistryTenantWorker implements Worker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    @Inject
    AuthService authService;

    @Override
    public boolean supports(Task task) {
        return task.getTaskType() == PROVISION_REGISTRY_TENANT;
    }

    @Transactional
    @Override
    public void execute(Task task) {
        ProvisionRegistryTenantTask provisionRegistryTenantTask = (ProvisionRegistryTenantTask) task;

        Optional<Registry> registryOptional = storage.getRegistryById(provisionRegistryTenantTask.getRegistryId());
        if (registryOptional.isEmpty()) {
            throw new IllegalStateException("Registry not found.");
        }
        Registry registry = registryOptional.get();

        final AuthResource authResource = authService.createTenantAuthResources(registry.getId().toString(), registry.getAppUrl());

        TenantRequest tenantRequest = TenantRequest.builder()
                .authServerUrl(authResource.getServerUrl())
                .authClientId(authResource.getClientId())
                .build();


        RegistryDeployment registryDeployment = registry.getRegistryDeployment();
        TenantManager tenantManager = TenantManager.builder()
                .tenantManagerUrl(registryDeployment.getTenantManagerUrl())
                .registryDeploymentUrl(registryDeployment.getRegistryDeploymentUrl())
                .build();

        Tenant tenant = tmClient.createTenant(tenantManager, tenantRequest);

        registry.setTenantId(tenant.getId());
        registry.setAppUrl(tenant.getTenantApiUrl());
        registry.getStatus().setLastUpdated(Instant.now());
        registry.getStatus().setStatus("AVAILABLE"); // TODO maybe wait for heartbeat

        storage.createOrUpdateRegistry(registry);

        // submit heartbeat task
        tasks.submit(RegistryHeartbeatTask.builder().registryId(registry.getId()).build());
    }
}
