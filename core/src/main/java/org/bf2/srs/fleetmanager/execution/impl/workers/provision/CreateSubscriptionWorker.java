package org.bf2.srs.fleetmanager.execution.impl.workers.provision;

import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.CreateSubscriptionTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementServiceException;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.CREATE_SUBSCRIPTION_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.CREATE_SUBSCRIPTION_W;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 */
@ApplicationScoped
public class CreateSubscriptionWorker extends AbstractWorker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    ResourceStorage storage;

    @Inject
    AccountManagementService accountManagementService;

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.enabled")
    boolean evalInstancesEnabled;

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.only")
    boolean evalInstancesOnlyEnabled;

    @ConfigProperty(name = "srs-fleet-manager.registry.instances.eval.max-count-per-user")
    int maxEvalInstancesPerUser;

    @Inject
    TaskManager tasks;

    public CreateSubscriptionWorker() {
        super(CREATE_SUBSCRIPTION_W);
    }

    @Override
    public boolean supports(Task task) {
        return CREATE_SUBSCRIPTION_T.name().equals(task.getType());
    }

    @Override
    @Transactional
    public void execute(Task aTask, WorkerContext ctl) throws Exception {
        CreateSubscriptionTask task = (CreateSubscriptionTask) aTask;
        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());

        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            ctl.retry();
        }

        AccountInfo accountInfo = task.getAccountInfo();
        RegistryData registry = registryOptional.get();

        //Only create a subscription if there is account information attached to the task.
        if (task.getAccountInfo() != null) {

            // Figure out if we are going to create a standard or eval instance.
            ResourceType resourceType = determineResourceType(accountInfo);

            // Try to consume some quota from AMS for the appropriate resource type (standard or eval).  If successful
            // we'll get back a subscriptionId - if not we'll throw an exception.
            String subscriptionId = accountManagementService.createResource(accountInfo, resourceType);

            // Convert to registry data
            RegistryInstanceTypeValueDto instanceType = resourceTypeToInstanceType(resourceType);
            registry.setSubscriptionId(subscriptionId);
            registry.setInstanceType(instanceType.value());

            // NOTE: Failure point 2
            storage.createOrUpdateRegistry(registry);
        }

        registry.setStatus(RegistryStatusValueDto.ACCEPTED.value());
        ctl.delay(() -> tasks.submit(ScheduleRegistryTask.builder().registryId(registry.getId()).build()));
    }

    private ResourceType determineResourceType(AccountInfo accountInfo) throws AccountManagementServiceException, EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException {
        final ResourceType resourceType = evalInstancesOnlyEnabled ?
                ResourceType.REGISTRY_INSTANCE_EVAL : accountManagementService.determineAllowedResourceType(accountInfo);

        if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
            checkEvalInstancesPermission(accountInfo);
        }

        return resourceType;
    }

    @Override
    @Transactional
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws Exception {
        CreateSubscriptionTask task = (CreateSubscriptionTask) aTask;

        // SUCCESS STATE
        Optional<RegistryData> registryOpt = storage.getRegistryById(task.getRegistryId());
        if (registryOpt.isPresent() && registryOpt.get().getStatus().equals(RegistryStatusValueDto.ACCEPTED.value()))
            return;

        if (registryOpt.isPresent()) {
            if (registryOpt.get().getSubscriptionId() != null) {
                accountManagementService.deleteSubscription(registryOpt.get().getSubscriptionId());
                log.warn("Returned subscription {} since something wen wrong during provisioning phase:", registryOpt.get().getSubscriptionId());
            }
            storage.deleteRegistry(task.getRegistryId());
        }
    }

    private void checkEvalInstancesPermission(AccountInfo accountInfo) throws EvalInstancesNotAllowedException, TooManyEvalInstancesForUserException {
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

    private static RegistryInstanceTypeValueDto resourceTypeToInstanceType(ResourceType resourceType) {
        return resourceType == ResourceType.REGISTRY_INSTANCE_STANDARD ? RegistryInstanceTypeValueDto.STANDARD : RegistryInstanceTypeValueDto.EVAL;
    }
}
