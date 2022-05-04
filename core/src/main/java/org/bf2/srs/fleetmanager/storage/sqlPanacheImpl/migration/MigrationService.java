package org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.migration;

import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.TenantNotFoundServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.model.Tenant;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
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
    TenantManagerService tenantManagerClient;

    /**
     * Temporal workaround for upgrade to the database schema
     * TODO remove this flag
     */
    @ConfigProperty(name = "srs.fleet.manager.delete.all")
    Optional<Boolean> deleteAll;


    public void runMigration() throws TenantManagerServiceException {

        if (deleteAll.isPresent() && deleteAll.get()) {

            log.warn("Removing all data first");

            TenantManagerConfig tm = TenantManagerConfig.builder()
                    .tenantManagerUrl("http://tenant-manager:8585")
                    .registryDeploymentUrl("https://service-registry-stage.apps.app-sre-stage-0.k3s7.p1.openshiftapps.com")
                    .build();

            for (Tenant t : tenantManagerClient.getAllTenants(tm)) {
                log.warn("Deleting tenant '{}'", t.getId());
                try {
                    tenantManagerClient.deleteTenant(tm, t.getId());
                } catch (TenantNotFoundServiceException ex) {
                    log.warn("Could not delete tenant '{}'. Tenant does not exist and may have been already deleted.", t.getId());
                }
            }

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
