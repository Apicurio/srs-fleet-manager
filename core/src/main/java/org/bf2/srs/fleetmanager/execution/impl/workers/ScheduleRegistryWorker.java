package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.model.RegistryDeploymentStatusValueRest;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.Registry;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static java.util.stream.Collectors.toList;
import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.SCHEDULE_REGISTRY_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.SCHEDULE_REGISTRY_W;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ScheduleRegistryWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    public ScheduleRegistryWorker() {
        super(SCHEDULE_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return SCHEDULE_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws StorageConflictException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        Optional<Registry> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            ctl.retry();
        }
        Registry registry = registryOptional.get();

        List<RegistryDeployment> eligibleRegistryDeployments = storage.getAllRegistryDeployments().stream()
                .filter(rd -> RegistryDeploymentStatusValueRest.fromValue(rd.getStatus().getValue()) == RegistryDeploymentStatusValueRest.AVAILABLE)
                .collect(toList());
        if (eligibleRegistryDeployments.isEmpty()) {
            // NOTE: Failure point 2
            // TODO How to report it better?
            log.warn("Could not schedule registry with ID {}. No deployments are available.", registry.getId());
            ctl.retry(100); // We can wait here longer, somebody needs to create a deployment
        }

        // Schedule to a random registry deployment
        // TODO Improve & use a specific scheduling strategy
        RegistryDeployment registryDeployment = eligibleRegistryDeployments.get(ThreadLocalRandom.current().nextInt(eligibleRegistryDeployments.size()));

        log.info("Scheduling {} to {}.", registry, registryDeployment); // TODO only available

        registry.setRegistryDeployment(registryDeployment);
        registry.getStatus().setLastUpdated(Instant.now());

        // NOTE: Failure point 3
        storage.createOrUpdateRegistry(registry);

        ctl.delay(() -> tasks.submit(ProvisionRegistryTenantTask.builder().registryId(registry.getId()).build()));
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, StorageConflictException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        // SUCCESS STATE
        Optional<Registry> registryOpt = storage.getRegistryById(task.getRegistryId());
        if (registryOpt.isPresent() && registryOpt.get().getRegistryDeployment() != null)
            return;

        // The only thing to handle is if we were able to schedule but storage does not work
        // In that case, the only thing to do is to just try deleting the registry.
        storage.deleteRegistry(task.getRegistryId());
    }
}
