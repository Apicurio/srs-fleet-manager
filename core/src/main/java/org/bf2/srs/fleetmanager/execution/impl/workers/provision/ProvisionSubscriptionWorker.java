package org.bf2.srs.fleetmanager.execution.impl.workers.provision;

import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.ProvisionSubscriptionTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.provision.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.workers.AbstractWorker;
import org.bf2.srs.fleetmanager.execution.manager.Task;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.execution.manager.WorkerContext;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryStatusValueDto;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.common.EvalInstancesNotAllowedException;
import org.bf2.srs.fleetmanager.spi.common.TooManyEvalInstancesForUserException;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.common.model.ResourceType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.bf2.srs.fleetmanager.execution.impl.tasks.TaskType.PROVISION_SUBSCRIPTION_T;
import static org.bf2.srs.fleetmanager.execution.impl.workers.WorkerType.PROVISION_SUBSCRIPTION_W;

/**
 * This class MUST be thread safe. It should not contain state and inject thread safe beans only.
 */
@ApplicationScoped
public class ProvisionSubscriptionWorker extends AbstractWorker {

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

    public ProvisionSubscriptionWorker() {
        super(PROVISION_SUBSCRIPTION_W);
    }

    @Override
    public boolean supports(Task task) {
        return PROVISION_SUBSCRIPTION_T.name().equals(task.getType());
    }

    @Override
    @Transactional
    public void execute(Task aTask, WorkerContext ctl) throws Exception {
        ProvisionSubscriptionTask task = (ProvisionSubscriptionTask) aTask;
        Optional<RegistryData> registryOptional = storage.getRegistryById(task.getRegistryId());
        if (registryOptional.isEmpty()) {
            // NOTE: Failure point 1
            ctl.retry();
        }

        AccountInfo accountInfo = task.getAccountInfo();
        RegistryData registry = registryOptional.get();

        // Figure out if we are going to create a standard or eval instance.
        ResourceType resourceType = evalInstancesOnlyEnabled ?
                ResourceType.REGISTRY_INSTANCE_EVAL : accountManagementService.determineAllowedResourceType(accountInfo);

        if (resourceType == ResourceType.REGISTRY_INSTANCE_EVAL) {
            checkEvalInstancesPermission(accountInfo);
        }

        // Try to consume some quota from AMS for the appropriate resource type (standard or eval).  If successful
        // we'll get back a subscriptionId - if not we'll throw an exception.
        String subscriptionId = accountManagementService.createResource(accountInfo, resourceType);

        // Convert to registry data
        RegistryInstanceTypeValueDto instanceType = resourceTypeToInstanceType(resourceType);
        registry.setSubscriptionId(subscriptionId);
        registry.setStatus(RegistryStatusValueDto.ACCEPTED.value());
        registry.setInstanceType(instanceType.value());

        // NOTE: Failure point 2
        storage.createOrUpdateRegistry(registry);

        ctl.delay(() -> tasks.submit(ScheduleRegistryTask.builder().registryId(registry.getId()).build()));
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

    @Override
    public void finallyExecute(Task aTask, WorkerContext ctl, Optional<Exception> error) throws Exception {
        ProvisionSubscriptionTask task = (ProvisionSubscriptionTask) aTask;

        // SUCCESS STATE
        Optional<RegistryData> registryOpt = storage.getRegistryById(task.getRegistryId());
        if (registryOpt.isPresent() && registryOpt.get().getStatus().equals(RegistryStatusValueDto.ACCEPTED.value()))
            return;

        //TODO handle subscription delete for some cases (e.g. when registry status is not accepted)
        storage.deleteRegistry(task.getRegistryId());
    }

    private static RegistryInstanceTypeValueDto resourceTypeToInstanceType(ResourceType resourceType) {
        return resourceType == ResourceType.REGISTRY_INSTANCE_STANDARD ? RegistryInstanceTypeValueDto.STANDARD : RegistryInstanceTypeValueDto.EVAL;
    }
}
