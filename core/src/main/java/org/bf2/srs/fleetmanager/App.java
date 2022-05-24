package org.bf2.srs.fleetmanager;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.Getter;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.bf2.srs.fleetmanager.operation.logging.sentry.SentryConfiguration;
import org.bf2.srs.fleetmanager.operation.metrics.UsageMetrics;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration.MigrationService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @ConfigProperty(name = "srs-fleet-manager.read-only-safe-mode", defaultValue = "false")
    @Getter
    boolean readOnlySafeMode;

    @Inject
    MigrationService migrationService;

    @Inject
    TaskManager taskManager;

    @Inject
    RegistryDeploymentService deploymentService;

    @Inject
    QuotaPlansService plansService;

    @Inject
    SentryConfiguration sentry;

    @Inject
    UsageMetrics usageMetrics;

    @Inject
    OperationContext ctx;

    void onStart(@Observes StartupEvent ev) throws Exception {
        if (readOnlySafeMode) {
            log.warn("Application is starting in a read-only safe mode.");
        }
        try {
            ctx.loadNewContextData();
            // NOTE: Ordering is important here
            sentry.init();
            if (!readOnlySafeMode) {
                migrationService.runMigration();
            } else {
                log.warn("Not starting the Migration Service. Application is in a read-only safe mode.");
            }
            usageMetrics.init();
            if (!readOnlySafeMode) {
                deploymentService.init();
            } else {
                log.warn("Not starting the Deployment(s) Service. Application is in a read-only safe mode.");
            }
            plansService.init();
            if (!readOnlySafeMode) {
                taskManager.start();
            } else {
                log.warn("Not starting the Task Manager. Application is in a read-only safe mode.");
            }
        } catch (Exception e) {
            log.error("Error starting fleet manager app", e);
            throw e;
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        if (!readOnlySafeMode) {
            taskManager.stop();
        }
    }
}
