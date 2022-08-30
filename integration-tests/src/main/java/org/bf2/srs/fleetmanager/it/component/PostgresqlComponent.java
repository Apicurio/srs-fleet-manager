package org.bf2.srs.fleetmanager.it.component;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.Getter;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class PostgresqlComponent extends AbstractComponent {

    private final String name;

    @Getter
    private String datasourceUrl;

    @Getter
    private String username;

    @Getter
    private String password;

    private EmbeddedPostgres database;

    public PostgresqlComponent(Environment env, String name) {
        super(LoggerFactory.getLogger(PostgresqlComponent.class), env);
        this.name = name;
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }
        database = EmbeddedPostgres
                .builder()
                .start();

        username = "postgres";
        password = username;
        datasourceUrl = database.getJdbcUrl(username, password);
        isRunning = true;
    }

    @Override
    public void stop() throws Exception {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }
        database.close();
        isRunning = false;
    }

    @Override
    public String getName() {
        return "postgresql-" + name;
    }

    @Override
    public boolean isContainer() {
        return true;
    }
}
