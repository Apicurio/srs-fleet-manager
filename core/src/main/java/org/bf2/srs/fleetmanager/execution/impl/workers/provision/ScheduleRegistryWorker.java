package org.bf2.srs.fleetmanager.execution.impl.workers.provision;

import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.SubscriptionNotFoundServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ScheduleRegistryWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    TaskManager tasks;

    @Inject
    AccountManagementService accountManagementService;

    public ScheduleRegistryWorker() {
        super(SCHEDULE_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return SCHEDULE_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            ctl.retry();
        }
        RegistryData registry = registryOptional.get();

        List<RegistryDeploymentData> eligibleRegistryDeployments = storage.getAllRegistryDeployments().stream()
                .filter(rd -> RegistryDeploymentStatusValue.of(rd.getStatus().getValue()) == RegistryDeploymentStatusValue.AVAILABLE)
                .collect(toList());

        if (eligibleRegistryDeployments.isEmpty()) {
            // NOTE: Failure point 2
            // TODO How to report it better?
            log.warn("Could not schedule registry with ID {}. No deployments are available.", registry.getId());
            ctl.retry(100); // We can wait here longer, somebody needs to create a deployment
        }

        // Schedule to a random registry deployment
        // TODO Improve & use a specific scheduling strategy
        RegistryDeploymentData registryDeployment = eligibleRegistryDeployments.get(ThreadLocalRandom.current().nextInt(eligibleRegistryDeployments.size()));

        log.info("Scheduling {} to {}.", registry, registryDeployment); // TODO only available

        registry.setRegistryDeployment(registryDeployment);
        registry.setStatus(RegistryStatusValueDto.PROVISIONING.value());

        // NOTE: Failure point 3
        storage.createOrUpdateRegistry(registry);

        ctl.delay(() -> tasks.submit(ProvisionRegistryTenantTask.builder().registryId(registry.getId()).build()));
    }

    @Override
    @Transactional
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, RegistryStorageConflictException, SubscriptionNotFoundServiceException, AccountManagementServiceException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        RegistryData registry = storage.getRegistryById(task.getRegistryId()).orElse(null);
        RegistryDeploymentData registryDeployment = null;

        if (registry != null)
            registryDeployment = registry.getRegistryDeployment();

        // SUCCESS STATE
        if (registry != null && registry.getRegistryDeployment() != null)
            return;

        //Cleanup orphan susbcription, if it's null, it's not needed since it will likely be an eval instance
        if (registry != null && registryDeployment != null && registry.getSubscriptionId() != null) {
            accountManagementService.deleteSubscription(registry.getSubscriptionId());
        }

        // Remove registry entity
        if (registry != null) {
            log.warn("Deleting registry data with registry {} and registry deployment {}", registry, registryDeployment);
            storage.deleteRegistry(registry.getId());
        }
    }
}
