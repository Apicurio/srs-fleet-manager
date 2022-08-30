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

package org.bf2.srs.fleetmanager.it.component;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class CompoundComponent extends AbstractComponent {

    public static final String C_KEYCLOAK = "C_KEYCLOAK";
    public static final String C_REGISTRY = "C_REGISTRY";
    public static final String C_AMS = "C_AMS";
    public static final String C_POSTGRESQL_TM = "C_POSTGRESQL_TM";
    public static final String C_TM = "C_TM";
    public static final String C_POSTGRESQL_FM = "C_POSTGRESQL_FM";
    public static final String C_FM1 = "C_FM1";
    public static final String C_FM2 = "C_FM2";

    private Deque<Pair<String, Component>> components = new LinkedList<>();

    public CompoundComponent() {
        super(LoggerFactory.getLogger(CompoundComponent.class), Environments.empty());
    }

    public void addAndStart(String name, Component component) throws Exception {
        components.add(Pair.of(name, component));
        component.start(); // Need to start immediately to get data for dependent components
    }

    @Override
    public boolean isRunning() {
        if (components.size() == 0)
            return false;
        return components.stream().allMatch(p -> p.getValue().isRunning());
    }

    public <T extends Component> Optional<T> get(String name, Class<T> clazz) {
        var opt = components.stream().filter(p -> p.getKey().equals(name)).findFirst();
        if (opt.isPresent()) {
            var val = opt.get().getValue();
            if (clazz.isInstance(val)) {
                return Optional.of((T) val);
            }
        }
        return Optional.empty();
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }
        for (Pair<String, Component> component : components) {
            component.getValue().start();
        }
    }

    @Override
    public String getName() {
        return "compound-component"; // TODO Print all component names
    }

    @Override
    public void stop() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopAndCollectLogs(String clazz, String testName) throws Exception {

        var it = components.descendingIterator();
        while (it.hasNext()) {
            it.next().getValue().stopAndCollectLogs(clazz, testName);
        }
        components.clear();
        isRunning = false;
    }
}
