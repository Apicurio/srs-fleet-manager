package org.bf2.srs.fleetmanager.it.component;

import lombok.Getter;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.AuthConfig;
import org.bf2.srs.fleetmanager.it.HttpUtils;
import org.bf2.srs.fleetmanager.it.executor.Exec;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class TenantManagerComponent extends AbstractComponent {

    private static final String TENANT_MANAGER_MODULE_PATH = "../multitenancy/api/";

    @Getter
    private final String tenantManagerUrl = "http://localhost:8585";

    @Getter
    private final AuthConfig authConfig;

    private Exec executor;

    /**
     * @param authConfig keep null if TM auth is disabled
     */
    public TenantManagerComponent(Environment env, AuthConfig authConfig) {
        super(LoggerFactory.getLogger(TenantManagerComponent.class), env);
        this.authConfig = authConfig;
    }

    public boolean isAuthEnabled() {
        return authConfig != null;
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }

        String path = getTenantManagerJarPath();
        logger.info("Starting Tenant Manager app from: {}", path);

        executor = new Exec();
        CompletableFuture.supplyAsync(() -> {
            try {

                List<String> cmd = new ArrayList<>();
                cmd.add("java");
                cmd.addAll(Arrays.asList(
                        "-jar", path));
                int timeout = executor.execute(cmd, env.getEnvVariables());
                return timeout == 0;
            } catch (Exception e) {
                logger.error("Failed to start tenant manager (could not find runner JAR).", e);
                System.exit(1);
                return false;
            }
        }, runnable -> new Thread(runnable).start());

        Awaitility.await("Tenant Manager is reachable").atMost(45, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .until(() -> HttpUtils.isReachable("localhost", 8585, "Tenant Manager"));

        Awaitility.await("Tenant Manager is ready").atMost(45, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .until(() -> HttpUtils.isReady(this.tenantManagerUrl, "/q/health/ready", false, "Tenant Manager"));
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
        return "tenant-manager";
    }

    @Override
    public String getStdOut() {
        return executor.stdOut();
    }

    @Override
    public String getStdErr() {
        return executor.stdErr();
    }


    private String getTenantManagerJarPath() {
        logger.info("Attempting to find tenant manager runner. Starting at cwd: " + new File("").getAbsolutePath());
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
