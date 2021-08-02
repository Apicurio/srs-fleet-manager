package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeploymentData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.REGISTRY_DEPLOYMENT_HEARTBEAT_W;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class RegistryDeploymentHeartbeatWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerClient tmClient;

    @Inject
    TaskManager tasks;

    public RegistryDeploymentHeartbeatWorker() {
        super(REGISTRY_DEPLOYMENT_HEARTBEAT_W);
    }

    @Override
    public boolean supports(Task task) {
        return REGISTRY_DEPLOYMENT_HEARTBEAT_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {
        RegistryDeploymentHeartbeatTask task = (RegistryDeploymentHeartbeatTask) aTask;

        Optional<RegistryDeploymentData> deploymentOptional = storage.getRegistryDeploymentById(task.getDeploymentId());
        if (deploymentOptional.isEmpty()) {
            // NOTE: Failure point 1
            // The Registry Deployment disappeared. Just retry.
            ctl.retry();
        }
        RegistryDeploymentData deployment = deploymentOptional.get();

        TenantManager tenantManager = TenantManager.builder()
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .registryDeploymentUrl(deployment.getRegistryDeploymentUrl())
                .build();

        boolean ok = tmClient.pingTenantManager(tenantManager);

        deployment.getStatus().setLastUpdated(Instant.now());

        if (!ok) {
            deployment.getStatus().setValue(RegistryDeploymentStatusValue.UNAVAILABLE.value());
            // TODO alerting?
            log.warn("Registry Deployment with ID {} has become unreachable.", deployment.getId());
        } else {
            deployment.getStatus().setValue(RegistryDeploymentStatusValue.AVAILABLE.value());
        }

        // NOTE: Failure point 2
        storage.createOrUpdateRegistryDeployment(deployment);
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        // The Registry Deployment was deleted or storage failed.
        // We should make sure the customers won't lose data so we'll just ignore this error.
    }
}
