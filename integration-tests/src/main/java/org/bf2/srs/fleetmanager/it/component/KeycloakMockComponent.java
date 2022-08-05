package org.bf2.srs.fleetmanager.it.component;

import lombok.Getter;
import org.bf2.srs.fleetmanager.it.AuthConfig;
import org.bf2.srs.fleetmanager.it.jwks.JWKSMockServer;
import org.slf4j.LoggerFactory;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public class KeycloakMockComponent extends AbstractComponent {

    private JWKSMockServer mock;

    @Getter
    private String baseUrl;

    @Getter
    private AuthConfig authConfig;

    public KeycloakMockComponent(Environment env) {
        super(LoggerFactory.getLogger(KeycloakMockComponent.class), env);
    }

    @Override
    public void start() throws Exception {
        if (isRunning) {
            logger.info("Component {} is already running. Skipping.", getName());
            return;
        }

        mock = new JWKSMockServer();
        baseUrl = mock.start();

        authConfig = AuthConfig.builder()
                .keycloakUrl(baseUrl + "/auth")
                .realm("test")
                .clientId("fleet-manager-client-id")
                .build();

        logger.info("keycloak mock running at {}", authConfig.getTokenEndpoint());
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
        return "keycloak-mock";
    }
}
