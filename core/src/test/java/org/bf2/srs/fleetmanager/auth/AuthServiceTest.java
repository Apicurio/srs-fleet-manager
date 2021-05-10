package org.bf2.srs.fleetmanager.auth;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bf2.srs.fleetmanager.auth.config.AuthConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@QuarkusTest
@QuarkusTestResource(AuthTestResource.class)
public class AuthServiceTest {

    @Inject
    AuthService authService;

    @Inject
    AuthConfig authConfig;

    Keycloak keycloak;

    @ConfigProperty(name = "keycloak.admin.password")
    String adminPassword;

    @ConfigProperty(name = "keycloak.admin.username")
    String adminUsername;

    @PostConstruct
    public void init() {

        keycloak = KeycloakBuilder.builder()
                .serverUrl(authConfig.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    @Test
    public void createResourcesTest() throws MalformedURLException, URISyntaxException {

        final AuthResource tenantAuthResource = authService.createTenantAuthResources(authConfig.getDataPlaneRealm(), "http://localhost:8080");
        final RealmResource tenantRealmResource = keycloak.realms()
                .realm(authConfig.getDataPlaneRealm());

        final List<String> clients = List.of(authConfig.getUiClientId(), authConfig.getApiClientId());

        assertTrue(tenantRealmResource.clients()
                .findAll()
                .stream()
                .filter( clientRepresentation -> clients.contains(clientRepresentation.getClientId()))
                .filter(clientRepresentation -> Arrays.asList(clientRepresentation.getDefaultRoles()).containsAll(authConfig.getRoles()))
                .map(ClientRepresentation::getClientId)
                .collect(Collectors.toList())
                .containsAll(clients));

        final URL authServerUrl = new URL(tenantAuthResource.getServerUrl());
        authServerUrl.toURI();

        authService.deleteResources(authConfig.getDataPlaneRealm());

        assertTrue(keycloak.realms().findAll().stream()
                .noneMatch(realmRepresentation -> realmRepresentation.getId().equals(authConfig.getDataPlaneRealm())));
    }
}