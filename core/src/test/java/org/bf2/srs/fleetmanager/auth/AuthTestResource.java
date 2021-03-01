package org.bf2.srs.fleetmanager.auth;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuthTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(AuthTestResource.class);

    private KeycloakContainer container;

    @Override
    public Map<String, String> start() {
        log.info("Starting Keycloak Test Container");

        container = new KeycloakContainer();
        container.start();

        final Map<String, String> props = new HashMap<>();
        props.put("auth.admin.server-url", container.getAuthServerUrl());
        props.put("auth.admin.realm", "master");
        props.put("auth.admin.client-id", "admin-cli");
        props.put("auth.realm.roles", "sr-admin,sr-developer, sr-readonly");
        props.put("auth.admin.username", container.getAdminUsername());
        props.put("auth.admin.password", container.getAdminPassword());

        return props;
    }

    @Override
    public void stop() {
        log.info("Stopping Keycloak Test Container");
        container.stop();
    }

}