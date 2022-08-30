package org.bf2.srs.fleetmanager.it.component;

import lombok.Getter;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public class DummyRegistryComponent extends AbstractComponent {


    @Getter
    private String baseUrl;

    public DummyRegistryComponent(Environment env) {
        super(LoggerFactory.getLogger(DummyRegistryComponent.class), env);
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }
        baseUrl = "http://localhost:3888";
        // NOOP
        isRunning = true;
    }

    @Override
    public void stop() {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }
        // NOOP
        isRunning = false;
    }

    @Override
    public String getName() {
        return "dummy-registry";
    }

}
