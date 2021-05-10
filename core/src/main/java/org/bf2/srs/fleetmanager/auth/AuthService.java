package org.bf2.srs.fleetmanager.auth;

import org.bf2.srs.fleetmanager.auth.config.AuthConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

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
                .realm(authConfig.getDataPlaneRealm())
                .clientId(authConfig.getDataPlaneClientId())
                .grantType(authConfig.getAdminGrantType())
                .clientSecret(authConfig.getDataPlaneClientSecret())
                .resteasyClient(client)
                .build();
    }

    /**
     * TODO How is failure reported?
     */
    public AuthResource createTenantAuthResources(String tenantId, String registryAppUrl) {

        final RealmResource realmResource = keycloak.realm(authConfig.getDataPlaneRealm());
        final ClientsResource clientsResource = realmResource.clients();

        final List<ClientRepresentation> clients = buildRealmClients(registryAppUrl, tenantId);

        clients.forEach(clientRepresentation -> {
            clientRepresentation.setDefaultRoles(authConfig.getRoles().toArray(new String[0]));
            clientsResource.create(clientRepresentation);
        });

        return AuthResource.builder()
                .clientId(authConfig.getApiClientId())
                .serverUrl(buildAuthServerUrl(authConfig.getDataPlaneRealm()))
                .build();
    }

    private String buildAuthServerUrl(String realm) {

        return String.format(AUTH_SERVER_PLACEHOLDER, authConfig.getAuthServerUrl(), realm);
    }

    private List<ClientRepresentation> buildRealmClients(String registryAppUrl, String tenantId) {

        final ClientRepresentation uiClient = new ClientRepresentation();
        uiClient.setClientId(authConfig.getUiClientId());
        uiClient.setName(authConfig.getUiClientId());
        uiClient.setRedirectUris(List.of(String.format(REDIRECT_URI_PLACEHOLDER, registryAppUrl)));
        uiClient.setPublicClient(true);

        final ClientRepresentation apiClient = new ClientRepresentation();
        apiClient.setClientId(authConfig.getApiClientId());
        apiClient.setName(authConfig.getApiClientId());
        apiClient.setBearerOnly(true);

        uiClient.setAttributes(Map.of("sr-tenant-id", tenantId));
        apiClient.setAttributes(Map.of("sr-tenant-id", tenantId));

        return List.of(uiClient, apiClient);
    }

    public void deleteResources(String realmId) {

        final RealmResource realmRepresentation = keycloak.realm(realmId);
        realmRepresentation.remove();
    }
}
