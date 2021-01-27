package io.bf2fc6cc711aee1a0c2a.auth.config;

import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@Getter
public class AuthConfig {

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

    @ConfigProperty(name = "auth.admin.grant-type")
    String adminGrantType;

    @ConfigProperty(name = "auth.tenant-id.prefix")
    String tenantIdPrefix;

    @Inject
    @ConfigProperty(name = "auth.realm.roles")
    List<String> roles;
}
