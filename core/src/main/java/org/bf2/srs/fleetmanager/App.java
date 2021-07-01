package org.bf2.srs.fleetmanager;

import org.bf2.srs.fleetmanager.execution.manager.TaskManager;
import org.bf2.srs.fleetmanager.rest.service.RegistryDeploymentService;
import org.bf2.srs.fleetmanager.storage.StorageConflictException;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration.MigrationService;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class App {

    @Inject
    MigrationService migrationService;

    @Inject
    TaskManager taskManager;

    @Inject
    RegistryDeploymentService deploymentService;

    void onStart(@Observes StartupEvent ev) throws StorageConflictException, IOException {
        migrationService.runMigration();
        taskManager.start();
        deploymentService.init();
    }

    void onStop(@Observes ShutdownEvent ev) {
        taskManager.stop();
    }
}
