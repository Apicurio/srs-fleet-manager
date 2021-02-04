package io.bf2fc6cc711aee1a0c2a.execution.impl.workers;

import io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import io.bf2fc6cc711aee1a0c2a.execution.manager.Task;
import io.bf2fc6cc711aee1a0c2a.execution.manager.TaskManager;
import io.bf2fc6cc711aee1a0c2a.execution.manager.WorkerContext;
import io.bf2fc6cc711aee1a0c2a.rest.model.RegistryDeploymentStatusValueRest;
import io.bf2fc6cc711aee1a0c2a.spi.TenantManagerClient;
import io.bf2fc6cc711aee1a0c2a.spi.model.TenantManager;
import io.bf2fc6cc711aee1a0c2a.storage.ResourceStorage;
import io.bf2fc6cc711aee1a0c2a.storage.StorageConflictException;
import io.bf2fc6cc711aee1a0c2a.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.bf2fc6cc711aee1a0c2a.execution.impl.tasks.TaskType.REGISTRY_DEPLOYMENT_HEARTBEAT_T;
import static io.bf2fc6cc711aee1a0c2a.execution.impl.workers.WorkerType.REGISTRY_DEPLOYMENT_HEARTBEAT_W;

/**
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

        Optional<RegistryDeployment> deploymentOptional = storage.getRegistryDeploymentById(task.getDeploymentId());
        if (deploymentOptional.isEmpty()) {
            // NOTE: Failure point 1
            // The Registry Deployment disappeared. Just retry.
            ctl.retry();
        }
        RegistryDeployment deployment = deploymentOptional.get();

        TenantManager tenantManager = TenantManager.builder()
                .tenantManagerUrl(deployment.getTenantManagerUrl())
                .registryDeploymentUrl(deployment.getRegistryDeploymentUrl())
                .build();

        boolean ok = tmClient.pingTenantManager(tenantManager);

        deployment.getStatus().setLastUpdated(Instant.now());

        if (!ok) {
            deployment.getStatus().setValue(RegistryDeploymentStatusValueRest.UNAVAILABLE.value());
            // TODO alerting?
            log.warn("Registry Deployment with ID {} has become unreachable.", deployment.getId());
        } else {
            deployment.getStatus().setValue(RegistryDeploymentStatusValueRest.AVAILABLE.value());
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
