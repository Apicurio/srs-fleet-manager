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

package org.bf2.srs.fleetmanager.rest.service;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import org.gradle.api.UncheckedIOException;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * @author Fabian Martinez
 */
public class StaticRegistryDeploymentsTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        try {
            return Collections.singletonMap("registry.deployments.config.file", new File(getClass().getResource("deployments.yaml").toURI()).getAbsolutePath());
        } catch (URISyntaxException e) {
            throw new UncheckedIOException(e);
        }
    }

}
