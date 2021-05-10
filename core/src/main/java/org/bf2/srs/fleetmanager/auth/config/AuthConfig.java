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

    @ConfigProperty(name = "auth.data-plane.server-url")
    String authServerUrl;

    @ConfigProperty(name = "auth.data-plane.realm")
    String dataPlaneRealm;

    @ConfigProperty(name = "auth.data-plane.client-id")
    String dataPlaneClientId;

    @ConfigProperty(name = "auth.data-plane.client-secret")
    String dataPlaneClientSecret;

    @ConfigProperty(name = "auth.data-plane.grant-type")
    String adminGrantType;

    @ConfigProperty(name = "auth.data-plane.tenant-id.prefix")
    String tenantIdPrefix;

    @ConfigProperty(name = "auth.data-plane.api.client-id")
    String apiClientId;

    @ConfigProperty(name = "auth.data-plane.ui.client-id")
    String uiClientId;

    @Inject
    @ConfigProperty(name = "auth.roles")
    List<String> roles;

    @Inject
    @ConfigProperty(name = "auth.data-plane.disable.tls.verification")
    Optional<Boolean> disableTlsVerification;
}
