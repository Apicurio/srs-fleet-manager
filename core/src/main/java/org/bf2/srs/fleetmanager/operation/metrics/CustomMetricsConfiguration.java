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

package org.bf2.srs.fleetmanager.operation.metrics;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.bf2.srs.fleetmanager.common.metrics.Constants;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;

/**
 * @author Fabian Martinez
 */
@Singleton
public class CustomMetricsConfiguration {

    /**
     * Micrometer default http metrics will be removed after we migrate our dashboards and alerts to the new custom metrics "rest_requests"
     */
    @Deprecated
    private static final String REQUESTS_TIMER_METRIC = "http.server.requests";

    @Produces
    @Singleton
    public MeterFilter enableHistogram() {
        double factor = 1000000000; //to convert slos to seconds
        return new MeterFilter() {

            @Override
            public Id map(Id id) {
                if(id.getName().startsWith(REQUESTS_TIMER_METRIC)) {
                    List<Tag> tags = new ArrayList<>(id.getTags());
                    if (isServiceRegistryManagementApiCall(id)) {
                        tags.add(Tag.of("api", "serviceregistry_mgmt"));
                    }
                    //removing "uri" tag due to bug in micrometer
                    //micrometer registering uri like this uri="/api/serviceregistry_mgmt/v1/registries/ef8e3c82-9dc1-484b-813d-d2aa64bbb56c"
                    //this is really bad, there are ways to configure micrometer to make the uri generic, but we are not using uri in our alerts so just removing it is easier
                    tags.removeIf(t -> {
                        return "uri".equals(t.getKey());
                    });
                    return id.replaceTags(tags);
                }
                return id;
            }

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if(id.getName().startsWith(Constants.REST_REQUESTS)) {
                    return DistributionStatisticConfig.builder()
                        .percentiles(0.5, 0.95, 0.99)
                        .serviceLevelObjectives(0.1 * factor, 1.0 * factor, 2.0 * factor, 5.0 * factor, 10.0 * factor, 30.0 * factor)
                        .build()
                        .merge(config);
                } else if(id.getName().startsWith(REQUESTS_TIMER_METRIC)) {
                    return DistributionStatisticConfig.builder()
                        .percentiles(0.5, 0.95, 0.99)
                        .serviceLevelObjectives(0.1 * factor, 1.0 * factor, 2.0 * factor, 5.0 * factor, 10.0 * factor, 30.0 * factor)
                        .build()
                        .merge(config);
                }
                return config;
            }
        };
    }

    private boolean isServiceRegistryManagementApiCall(Meter.Id id) {
        String uri = id.getTag("uri");
        return uri != null && uri.startsWith("/api/serviceregistry_mgmt");
    }

}
