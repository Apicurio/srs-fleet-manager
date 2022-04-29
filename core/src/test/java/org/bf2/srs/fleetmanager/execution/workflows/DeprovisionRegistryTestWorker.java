package org.bf2.srs.fleetmanager.execution.workflows;

import io.quarkus.arc.profile.IfBuildProfile;
import lombok.Getter;
import org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.DeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@IfBuildProfile("test")
@ApplicationScoped
public class DeprovisionRegistryTestWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Getter
    private static final AtomicBoolean hasBeenExecuted = new AtomicBoolean(false);

    @Getter
    private static final AtomicBoolean enabled = new AtomicBoolean(false);

    public DeprovisionRegistryTestWorker() {
        super(WorkerType.DEPROVISION_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return TaskType.DEPROVISION_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException, RegistryNotFoundException {
        log.debug("Executing org.bf2.srs.fleetmanager.execution.workflows.DeprovisionRegistryTestWorker.");
        if(!enabled.get()) {
            log.debug("DeprovisionRegistryTestWorker is DISABLED, skipping the 'execute' method.");
            return;
        }

        var task = (DeprovisionRegistryTask) aTask;
        var registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isPresent()) {
            var registry = registryOptional.get();
            storage.deleteRegistry(registry.getId());
        } else {
            ctl.stop();
        }
    }

    @Transactional
    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) {
        if(!enabled.get()) {
            log.debug("DeprovisionRegistryTestWorker is DISABLED, skipping the 'finallyExecute' method.");
            return;
        }
        log.debug("Setting org.bf2.srs.fleetmanager.execution.workflows.DeprovisionRegistryTestWorker.hasBeenExecuted to true.");
        hasBeenExecuted.set(true);
    }
}
