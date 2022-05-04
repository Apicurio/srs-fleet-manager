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

import io.quarkus.arc.DefaultBean;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.service.model.QuotaPlan;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Fabian Martinez
 */
@ApplicationScoped
@DefaultBean // For dev & test profiles
public class MockQuotaPlansService implements QuotaPlansService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, QuotaPlan> plans = new ConcurrentHashMap<>(1);

    @ConfigProperty(name = "registry.quota.plans.default", defaultValue = "default")
    String defaultQuotaPlan;

    @Override
    public void init() {
        log.debug("Using MockQuotaPlansService implementation of QuotaPlansService");
        plans.put(defaultQuotaPlan, QuotaPlan.builder()
                .name(defaultQuotaPlan)
                .resources(List.of())
                .build());
    }

    @Override
    public QuotaPlan determineQuotaPlan(String orgId) {
        return plans.get(defaultQuotaPlan);
    }
}
