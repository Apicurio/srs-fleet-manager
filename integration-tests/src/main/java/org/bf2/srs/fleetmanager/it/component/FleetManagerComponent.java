package org.bf2.srs.fleetmanager.it.component;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.bf2.srs.fleetmanager.it.HttpUtils;
import org.bf2.srs.fleetmanager.it.executor.Exec;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class FleetManagerComponent extends AbstractComponent {

    private static final String FLEET_MANAGER_JAR_PATH = "../core/target/srs-fleet-manager-core-%s-runner.jar";
    private static final String PROJECT_VERSION = System.getProperty("project.version");

    private final int port;

    private Exec executor;
    private final String nameSuffix;

    public FleetManagerComponent(Environment env, int port, String nameSuffix) {
        super(LoggerFactory.getLogger(FleetManagerComponent.class), env);
        this.port = port;
        this.nameSuffix = nameSuffix;
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }
        env = Environment.create().inherit(env).set("QUARKUS_HTTP_PORT", String.valueOf(port));

        String path = getFleetManagerJarPath();
        executor = new Exec();
        logger.info("Starting srs-fleet-manager app from: {}", path);
        CompletableFuture.supplyAsync(() -> {
            try {

                List<String> cmd = new ArrayList<>();
                cmd.add("java");
                cmd.addAll(Arrays.asList(
                        //"-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005",
                        "-jar", path));
                int timeout = executor.execute(cmd, env.getEnvVariables());
                return timeout == 0;
            } catch (Exception e) {
                logger.error("Failed to start fleet manager (could not find runner JAR).", e);
                System.exit(1);
                return false;
            }
        }, runnable -> new Thread(runnable).start());

        try {
            Awaitility.await("fleet manager is reachable on port " + port).atMost(90, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> HttpUtils.isReachable("localhost", port, "fleet manager"));

            Awaitility.await("fleet manager is ready on port " + port).atMost(90, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                    .until(() -> HttpUtils.isReady("http://localhost:" + port, "/q/health/ready", false, "fleet manager"));
        } catch (ConditionTimeoutException ex) {
            for (String s : executor.stdOut().split("\n")) {
                logger.info("[STDOUT] {}", s);
            }
            for (String s : executor.stdErr().split("\n")) {
                logger.info("[STDERR] {}", s);
            }
            throw ex;
        }
        isRunning = true;
    }

    @Override
    public void stop() {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }
        executor.stop();
        isRunning = false;
    }

    @Override
    public String getName() {
        return "fleet-manager-" + nameSuffix;
    }

    @Override
    public String getStdOut() {
        return executor.stdOut();
    }

    @Override
    public String getStdErr() {
        return executor.stdErr();
    }


    public String getFleetManagerUri() {
        return "http://localhost:" + port;
    }

    private String getFleetManagerJarPath() throws IOException {
        String path = String.format(FLEET_MANAGER_JAR_PATH, PROJECT_VERSION);
        logger.info("Checking runner JAR path: " + path);
        if (!runnerExists(path)) {
            logger.info("No runner JAR found.");
            throw new IllegalStateException("Could not find the executable jar for the server at '" + path + "'. " +
                    "This may happen if you are using an IDE to debug. Try to build the jars manually before running the tests.");
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
}
