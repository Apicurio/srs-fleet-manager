package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.RegistryHeartbeatTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.REGISTRY_HEARTBEAT_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.REGISTRY_HEARTBEAT_W;
import static org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto.FAILED;
import static org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto.READY;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
// TODO This task is (temporarily) not used. Enable when needed.
@ApplicationScoped
public class RegistryHeartbeatWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tms;

    public RegistryHeartbeatWorker() {
        super(REGISTRY_HEARTBEAT_W);
    }

    @Override
    public boolean supports(Task task) {
        return REGISTRY_HEARTBEAT_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException {
        var task = (RegistryHeartbeatTask) aTask;
        var registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isPresent()) {
            var registry = registryOptional.get();
            var status = RegistryStatusValueDto.of(registry.getStatus());
            switch (status) {
                case ACCEPTED: {
                    log.warn("Unexpected status '{}'. Stopping.", status);
                    ctl.stop();
                    return; // Unreachable
                }
                case PROVISIONING:
                case READY: {
                    var tmc = Utils.createTenantManagerConfig(registry.getRegistryDeployment());
                    boolean isAvailable = tms.pingTenant(tmc, registry.getId());

                    if (isAvailable) {
                        registry.setStatus(READY.value());
                        log.debug("Registry id='{}' is available.", registry.getId());
                    } else {
                        registry.setStatus(FAILED.value());
                        log.warn("Registry id='{}' is not available.", registry.getId());
                        // TODO Set failed_reason
                    }

                    if (status != RegistryStatusValueDto.of(registry.getStatus())) {
                        storage.createOrUpdateRegistry(registry);
                    }
                    return;
                }
                case FAILED: // TODO Decide based on failed_reason
                case REQUESTED_DEPROVISIONING:
                case DEPROVISIONING_DELETING:
                    log.debug("Registry has '{}' status. Stopping.", status);
                    ctl.stop();
                    return; // Unreachable
                default:
                    throw new IllegalStateException("Unexpected value: " + status);
            }
        } else {
            log.warn("Registry id='{}' not found. Stopping.", task.getRegistryId());
            ctl.stop();
        }
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        // NOOP
    }
}
