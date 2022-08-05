package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryDeploymentHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.RegistryDeploymentStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
// TODO This task is (temporarily) not used. Enable when needed.
@ApplicationScoped
public class RegistryDeploymentHeartbeatWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tms;

    public RegistryDeploymentHeartbeatWorker() {
        super(REGISTRY_DEPLOYMENT_HEARTBEAT_W);
    }

    @Override
    public boolean supports(Task task) {
        return REGISTRY_DEPLOYMENT_HEARTBEAT_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryDeploymentStorageConflictException, RegistryDeploymentNotFoundException {
        var task = (RegistryDeploymentHeartbeatTask) aTask;
        var deploymentOptional = storage.getRegistryDeploymentById(task.getDeploymentId());
        if (deploymentOptional.isPresent()) {
            var deployment = deploymentOptional.get();
            var status = RegistryDeploymentStatusValue.of(deployment.getStatus().getValue());
            switch (status) {
                case PROCESSING:
                case AVAILABLE:
                case UNAVAILABLE: {
                    var tmc = Utils.createTenantManagerConfig(deployment);
                    boolean isAvailable = tms.pingTenantManager(tmc);

                    if (isAvailable) {
                        deployment.getStatus().setValue(RegistryDeploymentStatusValue.AVAILABLE.value());
                        log.debug("RegistryDeployment id='{}' is available.", deployment.getId());
                    } else {
                        deployment.getStatus().setValue(RegistryDeploymentStatusValue.UNAVAILABLE.value());
                        log.warn("RegistryDeployment id='{}' is not available.", deployment.getId());
                    }

                    if (status != RegistryDeploymentStatusValue.of(deployment.getStatus().getValue())) {
                        storage.createOrUpdateRegistryDeployment(deployment);
                    }
                    return;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + status);
            }
        } else {
            log.warn("RegistryDeployment id='{}' not found. Stopping.", task.getDeploymentId());
            ctl.retry();
        }
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        // NOOP
    }
}
