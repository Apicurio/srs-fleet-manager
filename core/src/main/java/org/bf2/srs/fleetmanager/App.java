package org.bf2.srs.fleetmanager;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.bf2.srs.fleetmanager.common.Current;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.bf2.srs.fleetmanager.operation.logging.sentry.SentryConfiguration;
import org.bf2.srs.fleetmanager.operation.metrics.UsageMetrics;
import org.bf2.srs.fleetmanager.service.deployment.DeploymentLoader;
import org.bf2.srs.fleetmanager.service.quota.QuotaPlansService;
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
    DeploymentLoader deploymentLoader;

    @Inject
    SentryConfiguration sentry;

    @Inject
    UsageMetrics usageMetrics;

    @Inject
    OperationContext ctx;

    @Inject
    @Current
    QuotaPlansService quotaPlansService;

    void onStart(@Observes StartupEvent ev) throws Exception {
        try {
            ctx.loadNewContextData();
            // NOTE: Ordering is important here
            sentry.init();
            migrationService.runMigration();
            usageMetrics.init();
            deploymentLoader.run();
            quotaPlansService.start();
            taskManager.start();
        } catch (Exception e) {
            log.error("Error starting fleet manager app", e);
            throw e;
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        taskManager.stop();
    }
}
