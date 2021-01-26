package io.bf2fc6cc711aee1a0c2a.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RealmRepresentation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "auth.admin.server-url")
    String serverUrl;
    @ConfigProperty(name = "auth.admin.realm")
    String realm;
    @ConfigProperty(name = "auth.admin.client-id")
    String clientId;
    @ConfigProperty(name = "auth.admin.username")
    String username;
    @ConfigProperty(name = "auth.admin.password")
    String password;

    Keycloak keycloak;

    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }

    public RealmRepresentation createTenantAuth(String tenantId) {
        final RealmRepresentation realmRepresentation = new RealmRepresentation();

        realmRepresentation.setDisplayName(tenantId);
        keycloak.realms()
                .create(realmRepresentation);

        return getRealm(tenantId);
    }

    public RealmRepresentation getRealm(String name) {

        return keycloak.realms()
                .realm(name)
                .toRepresentation();
    }
}
