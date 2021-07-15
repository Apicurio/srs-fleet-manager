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

package org.bf2.srs.fleetmanager.it;

import java.util.UUID;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian Martinez
 */
public class DeploymentManager implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentManager.class);

    private static boolean suiteStarted = false;

    private static TestInfraManager infra = TestInfraManager.getInstance();

    void startTestInfraIfNeeded(ExtensionContext context) throws Exception {
        if (!infra.isRunning()) {
            LOGGER.info("Starting testing infrastructure");
            try {
                infra.start();
                LOGGER.info("Testing infrastructure started");
            } catch (Exception e) {
                infra.stopAndCollectLogs(context.getRequiredTestClass(), context.getDisplayName());
                throw new IllegalStateException(e);
            }
        } else {
            LOGGER.info("Testing infrastructure already running");
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!suiteStarted) {
            suiteStarted = true;
            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(Namespace.GLOBAL).put(UUID.randomUUID().toString(), this);
        }

        startTestInfraIfNeeded(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        startTestInfraIfNeeded(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isPresent()) {
            LOGGER.error("Test failed with error:", context.getExecutionException().get());
        }
        if (context.getExecutionException().isPresent()) {
            infra.stopAndCollectLogs(context.getRequiredTestClass(), context.getDisplayName());
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // do nothing because we want to start registry one time for all test suite
    }

    @Override
    public void close() throws Throwable {
        if (infra.isRunning()) {
            LOGGER.info("Tear down testing infrastructure");
            infra.stopAndCollectLogs(this.getClass(), "shutDownSuite");
        }
    }



}
