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

import java.util.List;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bf2.srs.fleetmanager.common.metrics.Constants;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

/**
 * Utility class for programatically using Micrometer timers.
 *
 * @author Fabian Martinez
 */
@ApplicationScoped
public class TimerService {

    @Inject
    MeterRegistry registry;

    public <R> R time(String name, String description, Supplier<R> func){
        Timer.Sample sample = start();
        boolean error = false;
        try {
            return func.get();
        } catch (Throwable e) {
            error = true;
            throw e;
        } finally {
            record(name, description, List.of(Tag.of(Constants.TAG_ERROR, String.valueOf(error))), sample);
        }
    }

    public Sample start() {
        return Timer.start(registry);
    }

    public void record(String name, String description, Iterable<Tag> tags, Sample sample) {
        Timer timer = Timer
            .builder(name)
            .description(description)
            .tags(tags)
            .register(registry);

        sample.stop(timer);
    }

}
