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
        container.withRealmImportFile("test-realm.json");
        container.start();

        final Map<String, String> props = new HashMap<>();
        props.put("auth.data-plane.server-url", container.getAuthServerUrl());
        props.put("auth.data-plane.realm", "test-realm");
        props.put("auth.data-plane.client-id", "test-client");
        props.put("auth.roles", "sr-admin,sr-developer, sr-readonly");
        props.put("auth.data-plane.client-secret", "93e433c4-176d-4b85-8466-7dfc088f9714");
        props.put("keycloak.admin.username", container.getAdminUsername());
        props.put("keycloak.admin.password", container.getAdminPassword());

        return props;
    }

    @Override
    public void stop() {
        log.info("Stopping Keycloak Test Container");
        container.stop();
    }
}