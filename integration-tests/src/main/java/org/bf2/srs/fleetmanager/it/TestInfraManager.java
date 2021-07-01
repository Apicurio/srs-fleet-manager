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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.executor.Exec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

/**
 * @author Fabian Martinez
 */
public class TestInfraManager {

    static final Logger LOGGER = LoggerFactory.getLogger(TestInfraManager.class);

    private static final String FLEET_MANAGER_JAR_PATH = "../core/target/srs-fleet-manager-core-%s-runner.jar";
    private static final String PROJECT_VERSION = System.getProperty("project.version");
    private static final String TENANT_MANAGER_MODULE_PATH = "../apicurio-registry/multitenancy/tenant-manager-api/";
    private static final String DEPLOYMENTS_CONFIG_FILE = "./src/main/resources/deployments.yaml";

    private LinkedList<TestInfraProcess> processes = new LinkedList<>();

    private int fleetManagerPort = 8080;

    private String tenantManagerUrl = "http://localhost:8585";

    private static TestInfraManager instance;

    public static TestInfraManager getInstance() {
        if (instance == null) {
            instance = new TestInfraManager();
        }
        return instance;
    }

    private TestInfraManager() {
        //private constructor
    }

    public String getFleetManagerUri() {
        return "http://localhost:" + fleetManagerPort;
    }

    public String getTenantManagerUri() {
        return this.tenantManagerUrl;
    }

    public boolean isRunning() {
        return !processes.isEmpty();
    }

    /**
     * Method for starting the registry from a runner jar file. New process is created.
     */
    public void start() throws Exception {
        if (!processes.isEmpty()) {
            throw new IllegalStateException("Registry is already running");
        }

        Map<String, String> appEnv = new HashMap<>();
        appEnv.put("LOG_LEVEL", "DEBUG");
        appEnv.put("SRS_LOG_LEVEL", "DEBUG");

        //TODO when auth is enabled in staging run tests with auth enabled
//        runKeycloak(appEnv);

        runTenantManager(appEnv);

        String datasourceUrl = deployPostgresql("fleet-manager");
        appEnv.put("SERVICE_API_DATASOURCE_URL", datasourceUrl);
        appEnv.put("SERVICE_API_DATASOURCE_USERNAME", "postgres");
        appEnv.put("SERVICE_API_DATASOURCE_PASSWORD", "postgres");

        //set static deployments config file
        appEnv.put("REGISTRY_DEPLOYMENTS_CONFIG_FILE", DEPLOYMENTS_CONFIG_FILE);

        Map<String, String> node1Env = new HashMap<>(appEnv);
        runFleetManager(node1Env, "node-1", fleetManagerPort);

        int c2port = fleetManagerPort + 1;

        Map<String, String> node2Env = new HashMap<>(appEnv);
        runFleetManager(node2Env, "node-2", c2port);


        RestAssured.baseURI = getFleetManagerUri();
    }

    private String deployPostgresql(String name) throws IOException {
        EmbeddedPostgres database = EmbeddedPostgres
                .builder()
                .start();

        String datasourceUrl = database.getJdbcUrl("postgres", "postgres");

        processes.add(new TestInfraProcess() {

            @Override
            public String getName() {
                return "postgresql-" + name;
            }

            @Override
            public void close() throws Exception {
                database.close();
            }

            @Override
            public String getStdOut() {
                return "";
            }

            @Override
            public String getStdErr() {
                return "";
            }

            @Override
            public boolean isContainer() {
                return false;
            }
        });

        return datasourceUrl;
    }

    private void runTenantManager(Map<String, String> fleetManagerAppEnv) throws IOException {

        String datasourceUrl = deployPostgresql("tenant-manager");

        Map<String, String> appEnv = new HashMap<>();
        appEnv.put("DATASOURCE_URL", datasourceUrl);
        appEnv.put("DATASOURCE_USERNAME", "postgres");
        appEnv.put("DATASOURCE_PASSWORD", "postgres");

        //registry is not deployed in purpose, it may still work
        appEnv.put("REGISTRY_ROUTE_URL", "http://localhost:3888");

        appEnv.put("LOG_LEVEL", "DEBUG");

        String path = getTenantManagerJarPath();
        LOGGER.info("Starting Tenant Manager app from: {}", path);

        Exec executor = new Exec();
        CompletableFuture.supplyAsync(() -> {
            try {

                List<String> cmd = new ArrayList<>();
                cmd.add("java");
                cmd.addAll(Arrays.asList(
                        "-jar", path));
                int timeout = executor.execute(cmd, appEnv);
                return timeout == 0;
            } catch (Exception e) {
                LOGGER.error("Failed to start tenant manager (could not find runner JAR).", e);
                System.exit(1);
                return false;
            }
        }, runnable -> new Thread(runnable).start());

        processes.add(new TestInfraProcess() {

            @Override
            public String getName() {
                return "tenant-manager";
            }

            @Override
            public void close() throws Exception {
                executor.stop();
            }

            @Override
            public String getStdOut() {
                return executor.stdOut();
            }

            @Override
            public String getStdErr() {
                return executor.stdErr();
            }

            @Override
            public boolean isContainer() {
                return false;
            }

        });

        Awaitility.await("is reachable").atMost(30, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
            .until(() -> HttpUtils.isReachable("localhost", 8585, "Tenant Manager"));

        Awaitility.await("is ready").atMost(30, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
            .until(() -> HttpUtils.isReady(this.tenantManagerUrl, "/q/health/ready", false, "Tenant Manager"));
    }

    private void runFleetManager(Map<String, String> appEnv, String nameSuffix, int port) throws IOException {
        appEnv.put("QUARKUS_HTTP_PORT", String.valueOf(port));
        String path = getFleetManagerJarPath();
        Exec executor = new Exec();
        LOGGER.info("Starting srs-fleet-manager app from: {}", path);
        CompletableFuture.supplyAsync(() -> {
            try {

                List<String> cmd = new ArrayList<>();
                cmd.add("java");
                cmd.addAll(Arrays.asList("-jar", path));
                int timeout = executor.execute(cmd, appEnv);
                return timeout == 0;
            } catch (Exception e) {
                LOGGER.error("Failed to start fleet manager (could not find runner JAR).", e);
                System.exit(1);
                return false;
            }
        }, runnable -> new Thread(runnable).start());
        processes.add(new TestInfraProcess() {

            @Override
            public String getName() {
                return "fleet-manager-" + nameSuffix;
            }

            @Override
            public void close() throws Exception {
                executor.stop();
            }

            @Override
            public String getStdOut() {
                return executor.stdOut();
            }

            @Override
            public String getStdErr() {
                return executor.stdErr();
            }

            @Override
            public boolean isContainer() {
                return false;
            }

        });

        Awaitility.await("is reachable").atMost(30, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
            .until(() -> HttpUtils.isReachable("localhost", port, "fleet manager"));

        Awaitility.await("is ready").atMost(30, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
            .until(() -> HttpUtils.isReady("http://localhost:" + port, "/q/health/ready", false, "fleet manager"));
    }

    private String getFleetManagerJarPath() throws IOException {
        String path = String.format(FLEET_MANAGER_JAR_PATH, PROJECT_VERSION);
        LOGGER.info("Checking runner JAR path: " + path);
        if (!runnerExists(path)) {
            LOGGER.info("No runner JAR found.");
            throw new IllegalStateException("Could not determine where to find the executable jar for the server. " +
                "This may happen if you are using an IDE to debug.");
        }
        return path;
    }

    private boolean runnerExists(String path) throws IOException {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.isFile();
    }

    public void stopAndCollectLogs(Class<?> testClass, String testName) throws IOException {
        Path logsPath = Paths.get("target/logs/", testClass.getName(), testName);

        LOGGER.info("Stopping testing infrastructure");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        if (logsPath != null) {
            Files.createDirectories(logsPath);
        }

        processes.descendingIterator().forEachRemaining(p -> {
            //non containerized processes have to be stopped before being able to read log output
            if (!p.isContainer()) {
                try {
                    p.close();
                    Thread.sleep(3000);
                } catch (Exception e) {
                    LOGGER.error("Error stopping process " + p.getName(), e);
                }
            }
            if (logsPath != null) {
                try {
                    Path filePath = logsPath.resolve(currentDate + "-" + p.getName() + "-" + "stdout.log");
                    LOGGER.info("Storing registry logs to " + filePath.toString());
                    Files.write(filePath, p.getStdOut().getBytes(StandardCharsets.UTF_8));
                    String stdErr = p.getStdErr();
                    if (stdErr != null && !stdErr.isEmpty()) {
                        Path stderrFile = logsPath.resolve(currentDate + "-" + p.getName() + "-" + "stderr.log");
                        Files.write(stderrFile, stdErr.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            if (p.isContainer()) {
                try {
                    p.close();
                } catch (Exception e) {
                    LOGGER.error("Error stopping process " + p.getName(), e);
                }
            }
        });
        processes.clear();
    }

    private String getTenantManagerJarPath() {
        LOGGER.info("Attempting to find tenant manager runner. Starting at cwd: " + new File("").getAbsolutePath());
        String config = System.getenv("TENANT_MANAGER_MODULE_PATH");
        if (config != null) {
            return findRunner(new File(config), "jar");
        }
        return findRunner(findTenantManagerModuleDir(), "jar");
    }

    private File findTenantManagerModuleDir() {
        File file = new File(TENANT_MANAGER_MODULE_PATH);
        if (file.isDirectory()) {
            return file;
        }
        throw new IllegalStateException("Unable to locate tenant manager module");
    }

    private String findRunner(File mavenModuleDir, String extension) {
        File targetDir = new File(mavenModuleDir, "target");
        if (targetDir.isDirectory()) {
            File[] files = targetDir.listFiles();
            for (File file : files) {
                if (extension != null) {
                    if (file.getName().contains("-runner") && file.getName().endsWith("." + extension)) {
                        return file.getAbsolutePath();
                    }
                } else if (file.getName().endsWith("-runner")) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

}
