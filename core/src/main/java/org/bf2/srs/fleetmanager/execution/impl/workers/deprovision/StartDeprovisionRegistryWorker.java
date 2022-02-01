package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.config.ExecutionProperties;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.StartDeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class StartDeprovisionRegistryWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    @Inject
    ExecutionProperties props;

    public StartDeprovisionRegistryWorker() {
        super(WorkerType.START_DEPROVISION_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.START_DEPROVISION_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException {

        StartDeprovisionRegistryTask task = (StartDeprovisionRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();

            var force = registry.getCreatedAt()
                    .plus(props.getDeprovisionStuckInstanceTimeout())
                    .isAfter(Instant.now());
            if (force) {
                log.warn("Registry instance '{}' is forced to be deprovisioned.", registry);
            }

            var deprovision = force;

            var status = RegistryStatusValueDto.of(registry.getStatus());
            switch (status) {
                case ACCEPTED:
                case PROVISIONING:
                    if (!force) {
                        log.debug("Provisioning in progress. Retrying.");
                        ctl.retry();
                    }
                    break;
                case READY:
                case FAILED:
                    deprovision = true;
                    break;
                case REQUESTED_DEPROVISIONING:
                case DEPROVISIONING_DELETING:
                    if (!force) {
                        log.debug("Deprovisioning is already in progress. Stopping. Registry = {}", registry);
                        ctl.stop();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected status value: " + status);
            }

            if (deprovision) {
                registry.setStatus(RegistryStatusValueDto.DEPROVISIONING_DELETING.value());
                storage.createOrUpdateRegistry(registry); // FAILURE POINT 2
                ctl.delay(() -> tasks.submit(DeprovisionRegistryTask.builder().registryId(registry.getId()).build()));
            }

        } else {
            log.warn("Registry id='{}' not found. Stopping.", task.getRegistryId());
            ctl.stop();
        }
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, RegistryStorageConflictException {

        StartDeprovisionRegistryTask task = (StartDeprovisionRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) {
            var registry = registryOptional.get();
            // SUCCESS STATE
            if (RegistryStatusValueDto.DEPROVISIONING_DELETING.value().equals(registry.getStatus()))
                return;

            // FAILURE
            // Nothing to do, user can retry
            log.warn("Failed to start deprovisioning of Registry '{}'. Check the status to see if the instance is stuck.", registry);
        } else {
            log.warn("Could not find Registry (ID = {}).", task.getRegistryId());
        }
    }
}
