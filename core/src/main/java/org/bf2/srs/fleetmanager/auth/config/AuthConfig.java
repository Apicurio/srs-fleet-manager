package org.bf2.srs.fleetmanager.auth.config;

import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Getter
public class AuthConfig {


    /**
     * The URL of the authentication server.
     */
    @ConfigProperty(name = "auth.data-plane.server-url")
    String authServerUrl;

    /**
     * The name of the realm where the fleet manager will create the auth resources.
     */
    @ConfigProperty(name = "auth.data-plane.realm")
    String dataPlaneRealm;

    /**
     * The client id of the client that the fleet manager will use to connect to the auth server.
     */
    @ConfigProperty(name = "auth.data-plane.client-id")
    String dataPlaneClientId;

    /**
     * The secret to be used along with the client id.
     */
    @ConfigProperty(name = "auth.data-plane.client-secret")
    String dataPlaneClientSecret;

    /**
     * The grant type to be used, should be client_credentials.
     */
    @ConfigProperty(name = "auth.data-plane.grant-type")
    String adminGrantType;

    /**
     * The client id for the api that the fleet manager will create.
     */
    @ConfigProperty(name = "auth.data-plane.api.client-id")
    String apiClientId;

    /**
     * The client id for the ui that the fleet manager will create.
     */
    @ConfigProperty(name = "auth.data-plane.ui.client-id")
    String uiClientId;

    /**
     * The roles to be created by the fleet manager.
     */
    @Inject
    @ConfigProperty(name = "auth.roles")
    List<String> roles;

    /**
     * Either to disable or not tls verification, default to false.
     */
    @Inject
    @ConfigProperty(name = "auth.data-plane.disable.tls.verification")
    Optional<Boolean> disableTlsVerification;
}
