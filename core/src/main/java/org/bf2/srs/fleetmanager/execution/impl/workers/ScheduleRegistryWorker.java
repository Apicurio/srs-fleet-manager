package org.bf2.srs.fleetmanager.execution.impl.workers;

import org.bf2.srs.fleetmanager.execution.impl.tasks.ProvisionRegistryTenantTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDeploymentStatusValue;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.common.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryDeploymentData;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.ams.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.ams.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.enabled")
    boolean evalInstancesEnabled;

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.only")
    boolean evalInstancesOnlyEnabled;

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.max-count-per-user")
    int maxEvalInstancesPerUser;

    @Inject
    ConvertRegistry convertRegistry;

    public ScheduleRegistryWorker() {
        super(SCHEDULE_REGISTRY_W);
    }

    @Override
    public boolean supports(Task task) {
        return SCHEDULE_REGISTRY_T.name().equals(task.getType());
    }

    @Transactional
    @Override
    public void execute(Task aTask, WorkerContext ctl) throws RegistryStorageConflictException, EvalInstancesNotAllowedException, AccountManagementServiceException, TooManyEvalInstancesForUserException, TermsRequiredException, ResourceLimitReachedException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryData().getId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            ctl.retry();
        }

        RegistryData registry = createRegistry(registryOptional.get(), task.getAccountInfo());

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

    private RegistryData createRegistry(RegistryData registryData, AccountInfo accountInfo) throws EvalInstancesNotAllowedException, AccountManagementServiceException, TooManyEvalInstancesForUserException, TermsRequiredException, ResourceLimitReachedException, RegistryStorageConflictException {
        // Figure out if we are going to create a standard or eval instance.
        ResourceType resourceType = evalInstancesOnlyEnabled ?
                ResourceType.REGISTRY_INSTANCE_EVAL : accountManagementService.determineAllowedResourceType(accountInfo);

        if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
            // Are eval instances allowed?
            if (!evalInstancesEnabled) {
                throw new EvalInstancesNotAllowedException();
            }

            // Limit the # of eval instances per user.  Need to check storage for list of eval registry instances.
            List<RegistryData> registriesByOwner = storage.getRegistriesByOwner(accountInfo.getAccountUsername());
            int evalInstanceCount = 0;
            for (RegistryData ownedRegistry : registriesByOwner) { // TODO Perform a dedicated query
                if (RegistryInstanceTypeValueDto.EVAL.value().equals(ownedRegistry.getInstanceType())) {
                    evalInstanceCount++;
                }
            }
            if (evalInstanceCount >= maxEvalInstancesPerUser) {
                throw new TooManyEvalInstancesForUserException();
            }
        }

        // Try to consume some quota from AMS for the appropriate resource type (standard or eval).  If successful
        // we'll get back a subscriptionId - if not we'll throw an exception.
        String subscriptionId = accountManagementService.createResource(accountInfo, resourceType);

        // Convert to registry data and persist it in the DB.
        RegistryInstanceTypeValueDto instanceType = resourceTypeToInstanceType(resourceType);

        registryData.setSubscriptionId(subscriptionId);
        registryData.setStatus(RegistryStatusValueDto.ACCEPTED.value());
        registryData.setInstanceType(instanceType.value());

        return registryData;
    }

    private static RegistryInstanceTypeValueDto resourceTypeToInstanceType(ResourceType resourceType) {
        return resourceType == ResourceType.REGISTRY_INSTANCE_STANDARD ? RegistryInstanceTypeValueDto.STANDARD : RegistryInstanceTypeValueDto.EVAL;
    }

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws RegistryNotFoundException, RegistryStorageConflictException {
        ScheduleRegistryTask task = (ScheduleRegistryTask) aTask;

        // SUCCESS STATE
        Optional<RegistryData> registryOpt = storage.getRegistryById(task.getRegistryData().getId());
        if (registryOpt.isPresent() && registryOpt.get().getRegistryDeployment() != null)
            return;

        // The only thing to handle is if we were able to schedule but storage does not work
        // In that case, the only thing to do is to just try deleting the registry.
        storage.deleteRegistry(task.getRegistryData().getId());
    }
}
