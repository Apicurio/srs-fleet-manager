package org.bf2.srs.fleetmanager.execution.impl.workers.deprovision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.StartDeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public StartDeprovisionRegistryWorker() {
        super(WorkerType.START_DEPROVISION_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.START_DEPROVISION_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {

        StartDeprovisionRegistryTask task = (StartDeprovisionRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) { // FAILURE POINT 1

            var registry = registryOptional.get();
            var status = RegistryStatusValueDto.of(registry.getStatus());
            switch (status) {
                case ACCEPTED:
                case PROVISIONING:
                    log.debug("Provisioning in progress. Retrying.");
                    ctl.retry();
                    return; // Unreachable
                case READY:
                case FAILED: {
                    // Continue
                    // Since we're not waiting, skip directly to "deleting" status.
                    // registry.setStatus(RegistryStatusValue.REQUESTED_DEPROVISIONING.value());
                    registry.setStatus(RegistryStatusValueDto.DEPROVISIONING_DELETING.value());
                    storage.createOrUpdateRegistry(registry); // FAILURE POINT 2
                    ctl.delay(() -> tasks.submit(DeprovisionRegistryTask.builder().registryId(registry.getId()).build()));
                    return;
                }
                case REQUESTED_DEPROVISIONING:
                case DEPROVISIONING_DELETING:
                    log.debug("Deprovisioning is already in progress. Stopping.");
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

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, StorageConflictException {

        StartDeprovisionRegistryTask task = (StartDeprovisionRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) {
            var registry = registryOptional.get();
            // SUCCESS STATE
            if (RegistryStatusValueDto.DEPROVISIONING_DELETING.value().equals(registry.getStatus()))
                return;

            // FAILURE
            // Nothing to do, user can retry
            log.warn("Failed to start deprovisioning of Registry: {}", registry);
        } else {
            log.warn("Could not find Registry (ID = {}).", task.getRegistryId());
        }
    }
}
