package io.bf2fc6cc711aee1a0c2a.auth;

import io.bf2fc6cc711aee1a0c2a.auth.config.AuthConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * TODO Add deletion of auth resources for error recovery
 */
@ApplicationScoped
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    AuthConfig authConfig;

    Keycloak keycloak;

    private static final String AUTH_SERVER_PLACEHOLDER = "%s/realms/%s";
    private static final String REDIRECT_URI_PLACEHOLDER = "%s/*";

    @PostConstruct
    public void init() {

        ResteasyClient client = null;
        if (authConfig.getDisableTlsVerification().orElseGet(() -> false)) {

            log.warn("Trusting all certificates. Do not use in production mode!");
            ResteasyClientBuilder builder = new ResteasyClientBuilderImpl();
            builder.setIsTrustSelfSignedCertificates(true);
            builder.hostnameVerification(HostnameVerificationPolicy.ANY);
            builder.disableTrustManager();
            client = builder.build();

        }

        keycloak = KeycloakBuilder.builder()
                .serverUrl(authConfig.getAuthServerUrl())
                .realm(authConfig.getAdminRealm())
                .clientId(authConfig.getAdminClientId())
                .grantType(authConfig.getAdminGrantType())
                .username(authConfig.getAdminUsername())
                .password(authConfig.getAdminPassword())
                .resteasyClient(client)
                .build();
    }

    /**
     * TODO How is failure reported?
     */
    public AuthResource createTenantAuthResources(String tenantId, String registryAppUrl) {

        final RealmRepresentation realmRepresentation = new RealmRepresentation();
        final String realmTenantId = authConfig.getTenantIdPrefix().concat("-").concat(tenantId);

        realmRepresentation.setDisplayName(realmTenantId);
        realmRepresentation.setRealm(realmTenantId);
        realmRepresentation.setAttributes(Map.of("sr-tenant-id", tenantId));
        realmRepresentation.setEnabled(true);

        realmRepresentation.setRoles(buildRealmRoles());
        realmRepresentation.setClients(buildRealmClients(registryAppUrl));

        keycloak.realms()
                .create(realmRepresentation);

        return AuthResource.builder()
                .clientId(authConfig.getApiClientId())
                .serverUrl(buildAuthServerUrl(realmTenantId))
                .build();
    }

    private String buildAuthServerUrl(String realm) {

        return String.format(AUTH_SERVER_PLACEHOLDER, authConfig.getAuthServerUrl(), realm);
    }

    private List<ClientRepresentation> buildRealmClients(String registryAppUrl) {

        final ClientRepresentation uiClient = new ClientRepresentation();
        uiClient.setClientId(authConfig.getUiClientId());
        uiClient.setName(authConfig.getUiClientId());
        uiClient.setRedirectUris(List.of(String.format(REDIRECT_URI_PLACEHOLDER, registryAppUrl)));
        uiClient.setPublicClient(true);
        //FIXME remove in the future
        uiClient.setDirectAccessGrantsEnabled(true);

        final ClientRepresentation apiClient = new ClientRepresentation();
        apiClient.setClientId(authConfig.getApiClientId());
        apiClient.setName(authConfig.getApiClientId());
        apiClient.setBearerOnly(true);

        return List.of(uiClient, apiClient);
    }

    private RolesRepresentation buildRealmRoles() {

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
