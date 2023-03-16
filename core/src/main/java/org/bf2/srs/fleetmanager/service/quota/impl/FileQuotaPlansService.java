/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.service.quota.impl;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.bf2.srs.fleetmanager.common.SerDesObjectMapperProducer;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.execution.impl.workers.Utils;
import org.bf2.srs.fleetmanager.service.quota.QuotaPlansService;
import org.bf2.srs.fleetmanager.service.quota.model.OrganizationAssignment;
import org.bf2.srs.fleetmanager.service.quota.model.QuotaPlan;
import org.bf2.srs.fleetmanager.service.quota.model.QuotaPlansConfigList;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantLimit;
import org.bf2.srs.fleetmanager.spi.tenants.model.UpdateTenantRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import static java.util.Objects.requireNonNull;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
@ApplicationScoped
public class FileQuotaPlansService implements QuotaPlansService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, QuotaPlan> plans = new ConcurrentHashMap<>();

    private Map<String, OrganizationAssignment> organizationAssignments = new ConcurrentHashMap<>();

    @Inject
    Validator validator;

    @Inject
    ResourceStorage storage;

    @Inject
    TenantManagerService tmClient;

    @ConfigProperty(name = "registry.quota.plans.config.file")
    Optional<File> plansConfigFile;

    @ConfigProperty(name = "registry.quota.plans.default", defaultValue = "default")
    String defaultQuotaPlan;

    public boolean isAvailable() {
        return !plansConfigFile.isEmpty();
    }

    @Override
    public void start() throws IOException {

        if (!isAvailable()) {
            throw new UnsupportedOperationException("FileQuotaPlansService is not available with the current configuration");
        }

        log.info("Loading registry quota plans config file from {}", plansConfigFile.get().getAbsolutePath());

        YAMLMapper mapper = SerDesObjectMapperProducer.getYAMLMapper();

        QuotaPlansConfigList quotaPlansConfigList = mapper.readValue(plansConfigFile.get(), QuotaPlansConfigList.class);

        List<QuotaPlan> staticQuotaPlans = quotaPlansConfigList.getPlans();

        Set<String> names = new HashSet<>();
        List<String> duplicatedNames = staticQuotaPlans.stream()
                .map(d -> {
                    Set<ConstraintViolation<QuotaPlan>> errors = validator.validate(d);
                    if (!errors.isEmpty()) {
                        throw new ConstraintViolationException(errors);
                    }
                    return d;
                })
                .filter(d -> !names.add(d.getName()))
                .map(d -> d.getName())
                .collect(Collectors.toList());
        if (!duplicatedNames.isEmpty()) {
            throw new IllegalArgumentException("Error in static quota plans config, duplicated plan name: " + duplicatedNames.toString());
        }

        if (!names.contains(defaultQuotaPlan)) {
            throw new IllegalArgumentException("Error in static quota plans config, default plan does not exist in plans config, default plan name: " + defaultQuotaPlan);
        }

        for (QuotaPlan p : staticQuotaPlans) {
            tmClient.validateConfig(p.getResources());
            plans.put(p.getName(), p);
        }

        List<OrganizationAssignment> staticOrganizationAssignments = quotaPlansConfigList.getOrganizations();
        if (staticOrganizationAssignments == null)
            staticOrganizationAssignments = Collections.emptyList();

        for (OrganizationAssignment assignment : staticOrganizationAssignments) {
            if (!plans.containsKey(assignment.getPlan())) {
                throw new IllegalStateException("Could not find quota plan named '" + assignment.getPlan() +
                        "' intended for organization ID '" + assignment.getOrgId() + "'");
            }
            organizationAssignments.put(assignment.getOrgId(), assignment);
        }

        if (quotaPlansConfigList.getReconcile() != null && quotaPlansConfigList.getReconcile()) {
            reconcile();
        }
    }

    private void reconcile() {
        log.info("Performing quota plan reconciliation");
        var allRegistries = storage.getAllRegistries();
        var updatedCount = 0;
        var errorCount = 0;
        for (RegistryData registry : allRegistries) {
            var tid = registry.getId();
            var tmc = Utils.createTenantManagerConfig(registry.getRegistryDeployment());
            try {
                var tenant = tmClient.getTenantById(tmc, tid).orElseThrow();
                Map<String, Long> tenantLimits = new HashMap<>();
                for (TenantLimit resource : tenant.getResources()) {
                    tenantLimits.put(resource.getType(), resource.getLimit());
                }

                var targetPlan = determineQuotaPlan(registry.getOrgId());

                var requiresUpdate = false;
                // Compare limits
                for (TenantLimit targetLimit : targetPlan.getResources()) {
                    var v = tenantLimits.get(targetLimit.getType());
                    if (v == null || !v.equals(targetLimit.getLimit())) {
                        requiresUpdate = true;
                        break;
                    }
                }
                if (requiresUpdate) {
                    UpdateTenantRequest utr = UpdateTenantRequest.builder()
                            .id(tid)
                            .status(tenant.getStatus())
                            .resources(targetPlan.getResources())
                            .build();
                    tmClient.updateTenant(tmc, utr);
                    updatedCount++;
                }

            } catch (Exception e) {
                errorCount++;
                log.error("Could not get or update tenant " + tid + " during quota plan reconciliation", e);
            }
        }
        if (errorCount == 0) {
            log.info("Quota plan reconciliation successful. Updated {} out of {} tenants",
                    updatedCount, allRegistries.size());
        } else {
            log.warn("Quota plan reconciliation finished with {} error(s). Updated {} out of {} tenants",
                    errorCount, updatedCount, allRegistries.size());
        }
    }

    @Override
    public QuotaPlan determineQuotaPlan(String orgId) {
        if (!isAvailable()) {
            throw new UnsupportedOperationException("FileQuotaPlansService is not available with the current configuration");
        }

        requireNonNull(orgId);
        var planName = defaultQuotaPlan;
        var assignment = organizationAssignments.get(orgId);
        if (assignment != null) {
            planName = assignment.getPlan();
        }
        return plans.get(planName);
    }
}
