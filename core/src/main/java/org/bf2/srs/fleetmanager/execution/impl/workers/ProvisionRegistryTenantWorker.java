package org.bf2.srs.fleetmanager.execution.impl.workers;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_REGISTRY_TENANT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.PROVISION_REGISTRY_TENANT_W;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.EvalInstanceExpirationRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.TaskSchedule;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ProvisionRegistryTenantWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.lifetime-seconds")
    Integer evalLifetimeSeconds;

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

    @Inject
    QuotaPlansService plansService;

    @Inject
    TaskManager tasks;

    @Inject
    AccountManagementService accountManagementService;

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
        // New approach: configure the deployment URL with a replacement like:  https://TENANT_ID.shrd.sr.openshift.com
        if (registryUrl.contains("TENANT_ID")) {
            registryUrl = registryUrl.replace("TENANT_ID", registry.getId());
        } else {
            // Old approach: configure the deployment URL without a replacement, and just add "/t/TENANT_ID" to the end of it.
            if (!registryUrl.endsWith("/")) {
                registryUrl += "/";
            }
            registryUrl += "t/" + registry.getId();
        }
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

        // Add expiration task if this is an eval instance
        if(isEvalInstance(registry.getInstanceType())) {
            var expiration = Instant.now().plus(Duration.ofSeconds(evalLifetimeSeconds));
            log.debug("Scheduling an expiration task for the eval instance {} to be executed at {}", registry, expiration);
            ctl.delay(() -> tasks.submit(EvalInstanceExpirationRegistryTask.builder()
                    .registryId(registry.getId())
                    .schedule(TaskSchedule.builder()
                            .firstExecuteAt(expiration)
                            .build())
                    .build()));
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

        //Cleanup orphan susbcription, if it's null, it's not needed since it will likely be an eval instance
        if (registry != null && registryDeployment != null && registry.getSubscriptionId() != null) {
            accountManagementService.deleteSubscription(registry.getSubscriptionId());
        }
        // Handle failures in "reverse" order

        // Cleanup orphan tenant
        if (registry != null && registryDeployment != null && task.getRegistryTenantId() != null) {
            tmClient.deleteTenant(Utils.createTenantManagerConfig(registryDeployment), registry.getId());
        }

        // Remove registry entity
        if (registry != null) {
            storage.deleteRegistry(registry.getId());
        }
    }

    private boolean isEvalInstance(String instanceType) {
        return RegistryInstanceTypeValueDto.of(instanceType) == RegistryInstanceTypeValueDto.EVAL;
    }
}
