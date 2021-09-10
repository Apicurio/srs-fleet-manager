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

package org.bf2.srs.fleetmanager.service.impl;

import java.io.File;
import java.io.IOException;
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

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.service.model.QuotaPlan;
import org.bf2.srs.fleetmanager.service.model.QuotaPlansConfigList;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * @author Fabian Martinez
 */
@ApplicationScoped
@IfBuildProfile("prod")
public class FileQuotaPlansService implements QuotaPlansService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, QuotaPlan> plans = new ConcurrentHashMap<>();

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

    public void init() throws IOException {
        log.debug("Using FileQuotaPlansService implementation of QuotaPlansService");

        if (plansConfigFile.isEmpty()) {
            throw new IllegalArgumentException("Error in static quota plans config: Property 'registry.quota.plans.config.file' is required.");
        }

        log.info("Loading registry quota plans config file from {}", plansConfigFile.get().getAbsolutePath());

        YAMLMapper mapper = new YAMLMapper();

        QuotaPlansConfigList quotaPlansConfigList = mapper.readValue(plansConfigFile.get(), QuotaPlansConfigList.class);

        List<QuotaPlan> staticQuotaPlans = quotaPlansConfigList.getPlans();

        Set<String> names = new HashSet<>();
        List<String> duplicatedNames = staticQuotaPlans.stream()
                .map(d-> {
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
    }

    @Override
    public QuotaPlan getDefaultQuotaPlan() {
        return plans.get(defaultQuotaPlan);
    }
}
