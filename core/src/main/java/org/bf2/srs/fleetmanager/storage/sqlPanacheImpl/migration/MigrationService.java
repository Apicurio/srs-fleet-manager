package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration;

import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.CleanResult;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MigrationService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    Flyway flyway;

    @Inject
    TenantManagerClient tenantManagerClient;

    public void runMigration() {
        // TODO Remove
        /*
        TenantManager tm = TenantManager.builder()
                .tenantManagerUrl("http://tenant-manager:8585") // TODO Hardcoded
                .registryDeploymentUrl("https://service-registry-stage.apps.app-sre-stage-0.k3s7.p1.openshiftapps.com") // TODO Hardcoded
                .build();
        tenantManagerClient.getAllTenants(tm).stream().forEach(t -> {
            log.warn("Deleting tenant '{}'", t.getId());
            tenantManagerClient.deleteTenant(tm, t.getId());
        });
        */
        CleanResult cleanResult = flyway.clean(); // TODO Remove
        log.info("Database clean result: " +
                        "flywayVersion = '{}', " +
                        "database = '{}', " +
                        "schemasCleaned = '{}', " +
                        "schemasDropped = '{}', " +
                        "warnings = '{}'.",
                cleanResult.flywayVersion,
                cleanResult.database,
                cleanResult.schemasCleaned,
                cleanResult.schemasDropped,
                cleanResult.warnings);
        MigrateResult migrateResult = flyway.migrate();
        log.info("Database migrate result: " +
                        "flywayVersion = '{}', " +
                        "database = '{}', " +
                        "initialSchemaVersion = '{}', " +
                        "targetSchemaVersion = '{}', " +
                        "migrations = '{}', " +
                        "warnings = '{}'.",
                migrateResult.flywayVersion,
                migrateResult.database,
                migrateResult.initialSchemaVersion,
                migrateResult.targetSchemaVersion,
                migrateResult.migrations,
                migrateResult.warnings);
    }
}
