package io.bf2fc6cc711aee1a0c2a.auth;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class AuthService {

    @ConfigProperty(name = "auth.admin.server-url")
    String authServerUrl;
    @ConfigProperty(name = "auth.admin.realm")
    String adminRealm;
    @ConfigProperty(name = "auth.admin.client-id")
    String adminClientId;
    @ConfigProperty(name = "auth.admin.username")
    String adminUsername;
    @ConfigProperty(name = "auth.admin.password")
    String adminPassword;

    @Inject
    @ConfigProperty(name = "auth.realm.roles")
    List<String> roles;

    Keycloak keycloak;

    @PostConstruct
    public void init() {

        keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(adminRealm)
                .clientId(adminClientId)
                .grantType("password")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public RealmResource createTenantAuthResources(String tenantId) {

        final RealmRepresentation realmRepresentation = new RealmRepresentation();

        realmRepresentation.setDisplayName(tenantId);

        realmRepresentation.setRoles(buildRealmRoles());

        //TODO rename realm to something better than just the tenant id
        keycloak.realms()
                .create(realmRepresentation);

        return keycloak.realms()
                .realm(tenantId);
    }


    public RolesRepresentation buildRealmRoles() {

        final RolesRepresentation rolesRepresentation = new RolesRepresentation();

        final List<RoleRepresentation> newRealmRoles = roles.stream()
                .map(r -> {
                    RoleRepresentation rp = new RoleRepresentation();
                    rp.setName(r);
                    return rp;
                })
                .collect(Collectors.toList());

        rolesRepresentation.setRealm(newRealmRoles);

        return rolesRepresentation;
    }
}
