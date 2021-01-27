package io.bf2fc6cc711aee1a0c2a.auth;

import io.bf2fc6cc711aee1a0c2a.auth.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class AuthService {

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

    public RealmResource createTenantAuthResources(String tenantId) {

        final RealmRepresentation realmRepresentation = new RealmRepresentation();

        final String realmTenantId = authConfig.getTenantIdPrefix().concat("-").concat(tenantId);

        realmRepresentation.setDisplayName(realmTenantId);
        realmRepresentation.setRoles(buildRealmRoles());
        realmRepresentation.setRealm(realmTenantId);

        final ClientRepresentation uiClient = new ClientRepresentation();
        uiClient.setClientId("apicurio-registry");
        final ClientRepresentation apiClient = new ClientRepresentation();
        apiClient.setClientId("registry-api");

        realmRepresentation.setClients(List.of(uiClient, apiClient));

        keycloak.realms()
                .create(realmRepresentation);


        return keycloak.realms()
                .realm(realmTenantId);
    }


    public RolesRepresentation buildRealmRoles() {

        final RolesRepresentation rolesRepresentation = new RolesRepresentation();

        final List<RoleRepresentation> newRealmRoles = authConfig.getRoles()
                .stream()
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
