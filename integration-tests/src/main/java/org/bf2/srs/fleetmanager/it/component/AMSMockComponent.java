package org.bf2.srs.fleetmanager.it.component;

import lombok.Getter;
import org.bf2.srs.fleetmanager.it.ams.AmsWireMockServer;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class AMSMockComponent extends AbstractComponent {

    private AmsWireMockServer mock;

    @Getter
    private String baseUrl;

    public AMSMockComponent(Environment env) {
        super(LoggerFactory.getLogger(AMSMockComponent.class), env);
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }
        mock = new AmsWireMockServer();
        baseUrl = mock.start();

        logger.info("ams mock running at {}", baseUrl);
        isRunning = true;
    }

    @Override
    public void stop() {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }
        mock.stop();
        isRunning = false;
    }

    @Override
    public String getName() {
        return "ams-mock";
    }
}
