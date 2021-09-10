package org.bf2.srs.fleetmanager.rest.service.impl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import org.apache.commons.lang3.tuple.Pair;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.auth.interceptor.CheckDeletePermissions;
import org.bf2.srs.fleetmanager.auth.interceptor.CheckReadPermissions;
import org.bf2.srs.fleetmanager.execution.impl.tasks.ScheduleRegistryTask;
import org.bf2.srs.fleetmanager.execution.impl.tasks.deprovision.StartDeprovisionRegistryTask;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryService;
import org.bf2.srs.fleetmanager.rest.service.convert.ConvertRegistry;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryInstanceTypeValueDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryListDto;
import org.bf2.srs.fleetmanager.rest.service.model.ServiceStatusDto;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ResourceLimitReachedException;
import org.bf2.srs.fleetmanager.spi.TermsRequiredException;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.spi.model.ResourceType;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.RegistryStorageConflictException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.PanacheRegistryRepository;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.util.BasicQuery;
import org.bf2.srs.fleetmanager.util.SearchQuery;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ValidationException;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.OWNER_ID_PLACEHOLDER;
import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

@ApplicationScoped
public class RegistryServiceImpl implements RegistryService {

    @Inject
    TaskManager tasks;

    @Inject
    ResourceStorage storage;

    @Inject
    ConvertRegistry convertRegistry;

    @Inject
    PanacheRegistryRepository registryRepository;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @Inject
    AccountManagementService accountManagementService;

    @ConfigProperty(name = "srs-fleet-manager.max.eval.instances", defaultValue = "1000")
    Integer maxEvalInstances;

    @Override
    public RegistryDto createRegistry(RegistryCreateDto registryCreate)
            throws RegistryStorageConflictException, TermsRequiredException, ResourceLimitReachedException {
        final AccountInfo accountInfo = authService.extractAccountInfo();

        // Figure out if we are going to create a standard or eval instance.
        ResourceType resourceType = accountManagementService.determineAllowedResourceType(accountInfo);

        // Try to consume some quota from AMS for the appropriate resource type (standard or eval).  If successful
        // we'll get back a subscriptionId - if not we'll throw an exception.
        String subscriptionId = accountManagementService.createResource(accountInfo, resourceType);

        // Convert to registry data and persist it in the DB.
        RegistryInstanceTypeValueDto instanceType = resourceTypeToInstanceType(resourceType);

        RegistryData registryData = convertRegistry.convert(registryCreate, subscriptionId, accountInfo.getAccountUsername(),
                accountInfo.getOrganizationId(), accountInfo.getAccountId(), instanceType);
        // Generate the ID
        registryData.setId(UUID.randomUUID().toString());
        storage.createOrUpdateRegistry(registryData);
        tasks.submit(ScheduleRegistryTask.builder().registryId(registryData.getId()).build());
        return convertRegistry.convert(registryData);
    }

    private static RegistryInstanceTypeValueDto resourceTypeToInstanceType(ResourceType resourceType) {
        return resourceType == ResourceType.REGISTRY_INSTANCE_STANDARD ? RegistryInstanceTypeValueDto.STANDARD : RegistryInstanceTypeValueDto.EVAL;
    }

    @Override
    public RegistryListDto getRegistries(Integer page, Integer size, String orderBy, String search) {
        // Defaults
        var sort = Sort.by("id", Sort.Direction.Ascending);
        page = (page != null) ? page : 1;
        size = (size != null) ? size : 10;

        if (orderBy != null) {
            var order = orderBy.split(" ");
            if (order.length != 2) {
                throw new ValidationException("invalid orderBy");
            }
            if ("asc".equals(order[1])) {
                sort = Sort.by(order[0], Sort.Direction.Ascending);
            } else {
                sort = Sort.by(order[0], Sort.Direction.Descending);
            }
        }

        List<Pair<String, Object>> conditions = new ArrayList<>();
        if (search != null && !search.isEmpty()) {
            var basicQuery = new BasicQuery(search, Arrays.asList("name", "status"));
            conditions.add(Pair.of(basicQuery.getColumn(), basicQuery.getArgument()));
        }

        //list only registries from your organization or the ones the user owns
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            String orgId = accountInfo.getOrganizationId();
            if (orgId != null) {
                conditions.add(Pair.of("orgId", orgId));
            } else {
                conditions.add(Pair.of("ownerId", accountInfo.getAccountId()));
            }
        } else {
            conditions.add(Pair.of("ownerId", OWNER_ID_PLACEHOLDER));
        }

        var query = new SearchQuery(conditions);

        long total = this.registryRepository.count(query.getQuery(), query.getArguments());
        PanacheQuery<RegistryData> itemsQuery = this.registryRepository.find(query.getQuery(), sort, query.getArguments());

        var items = itemsQuery.page(Page.of(page - 1, size)).stream().map(convertRegistry::convert)
                .collect(Collectors.toList());
        return RegistryListDto.builder().items(items)
                .page(page)
                .size(size)
                .total(total).build();
    }

    @Override
    @CheckReadPermissions
    public RegistryDto getRegistry(String registryId) throws RegistryNotFoundException {
        try {
            return storage.getRegistryById(registryId)
                    .map(convertRegistry::convert)
                    .orElseThrow(() -> new RegistryNotFoundException(registryId));
        } catch (NumberFormatException ex) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    @Override
    @CheckDeletePermissions
    public void deleteRegistry(String registryId) throws RegistryNotFoundException, RegistryStorageConflictException {
        try {
            // Verify preconditions - Registry exists
            storage.getRegistryById(registryId).orElseThrow(() -> new RegistryNotFoundException(registryId));
            tasks.submit(StartDeprovisionRegistryTask.builder().registryId(registryId).build());
        } catch (NumberFormatException ex) {
            throw new RegistryNotFoundException(registryId);
        }
    }

    @Override
    public ServiceStatusDto getServiceStatus() {
        List<Pair<String, Object>> conditions = new ArrayList<>();
        conditions.add(Pair.of("instance_type", RegistryInstanceTypeValueDto.EVAL.value()));
        var query = new SearchQuery(conditions);
        long total = this.registryRepository.count(query.getQuery(), query.getArguments());

        ServiceStatusDto status = new ServiceStatusDto();
        status.setMaxEvalInstancesReached(total >= maxEvalInstances);
        return status;
    }

}
