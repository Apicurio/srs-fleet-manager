package org.bf2.srs.fleetmanager.auth;

import org.bf2.srs.fleetmanager.auth.config.AuthConfig;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
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

    @PostConstruct
    public void init() {

        keycloak = KeycloakBuilder.builder()
                .serverUrl(authConfig.getAuthServerUrl())
                .realm(authConfig.getAdminRealm())
                .clientId(authConfig.getAdminClientId())
                .grantType(authConfig.getAdminGrantType())
                .username(authConfig.getAdminUsername())
                .password(authConfig.getAdminPassword())
                .build();
    }

    @Test
    public void createResourcesTest() throws MalformedURLException, URISyntaxException {

        final String realm = "test-tenant-id";
        final String realmId = authConfig.getTenantIdPrefix() + "-" + realm;

        final AuthResource tenantAuthResource = authService.createTenantAuthResources(realm, "http://localhost:8080");

        final RealmResource tenantRealmResource = keycloak.realms()
                .realm(realmId);

        assertTrue(tenantRealmResource.roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
                .containsAll(authConfig.getRoles()));

        final List<String> clients = List.of(authConfig.getUiClientId(), authConfig.getApiClientId());

        assertTrue(tenantRealmResource.clients()
                .findAll()
                .stream()
                .map(ClientRepresentation::getClientId)
                .collect(Collectors.toList())
                .containsAll(clients));

        final URL authServerUrl = new URL(tenantAuthResource.getServerUrl());
        authServerUrl.toURI();

        authService.deleteResources(realmId);

        assertTrue(keycloak.realms().findAll().stream()
                .noneMatch(realmRepresentation -> realmRepresentation.getId().equals(realmId)));
    }
}