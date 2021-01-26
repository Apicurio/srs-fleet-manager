package io.bf2fc6cc711aee1a0c2a.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    @Inject
    @ConfigProperty(name = "auth.realm.roles")
    List<String> roles;

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

    public boolean createTenantAuthResources(String tenantId) {


        return false;
    }


    /**
     * Add realm roles
     *
     * @param realm name
     */
    public boolean addRealmRoles(String realm, List<String> roles) {

        return false;
    }
}
