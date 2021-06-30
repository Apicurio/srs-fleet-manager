package org.bf2.srs.fleetmanager;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration.MigrationService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    @Inject
    MigrationService migrationService;

    @Inject
    TaskManager taskManager;

    void onStart(@Observes StartupEvent ev) {
        migrationService.runMigration();
        taskManager.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        taskManager.stop();
    }
}
