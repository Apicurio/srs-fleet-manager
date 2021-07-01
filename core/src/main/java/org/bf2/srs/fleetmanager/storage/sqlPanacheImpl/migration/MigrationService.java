package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration;

import org.bf2.srs.fleetmanager.spi.TenantManagerClient;
import org.bf2.srs.fleetmanager.spi.model.TenantManager;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.CleanResult;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MigrationService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    Flyway flyway;

    @Inject
    TenantManagerClient tenantManagerClient;

    @Inject
    ResourceStorage storage;

    /**
     * Temporal workaround for upgrade to the database schema
     * TODO remove this flag
     */
    @ConfigProperty(name = "srs.fleet.manager.delete.all")
    Optional<Boolean> deleteAll;


    public void runMigration() {

        if (deleteAll.isPresent() && deleteAll.get()) {

            log.warn("Removing all data first");

            storage.getAllRegistryDeployments().forEach(d -> {
                TenantManager tm = TenantManager.builder()
                        .tenantManagerUrl(d.getTenantManagerUrl())
                        .registryDeploymentUrl(d.getRegistryDeploymentUrl())
                        .build();

                tenantManagerClient.getAllTenants(tm).stream().forEach(t -> {
                    log.warn("Deleting tenant '{}'", t.getId());
                    tenantManagerClient.deleteTenant(tm, t.getId());
                });
            });
            CleanResult cleanResult = flyway.clean();
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
        }

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
