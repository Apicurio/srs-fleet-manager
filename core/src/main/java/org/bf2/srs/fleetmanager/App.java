package org.bf2.srs.fleetmanager;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.operation.logging.sentry.SentryConfiguration;
import org.bf2.srs.fleetmanager.operation.metrics.UsageMetrics;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.service.QuotaPlansService;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    Logger log = LoggerFactory.getLogger(this.getClass());

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

    void onStart(@Observes StartupEvent ev) throws Exception {
        try {
            sentry.init();
            migrationService.runMigration();
            usageMetrics.init();
            taskManager.start();
            deploymentService.init();
            plansService.init();
        } catch (Exception e) {
            log.error("Error starting fleet manager app", e);
            throw e;
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        taskManager.stop();
    }
}
