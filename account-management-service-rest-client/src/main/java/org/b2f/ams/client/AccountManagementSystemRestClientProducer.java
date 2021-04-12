package org.b2f.ams.client;

import io.quarkus.arc.profile.UnlessBuildProfile;
import org.b2f.ams.client.auth.Auth;
import org.b2f.ams.client.auth.KeycloakAuth;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;

@ApplicationScoped
public class AccountManagementSystemRestClientProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "account-management-system.url")
    private String endpoint;

    @ConfigProperty(name = "sso.url")
    private String ssoUrl;

    @ConfigProperty(name = "sso.realm")
    private String ssoRealm;

    @ConfigProperty(name = "sso.client-id")
    private String ssoClientId;

    @ConfigProperty(name = "sso.client-secret")
    private String ssoClientSecret;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementSystemRestClient produce() {
        log.info("Using Account Management System REST client.");

        final Auth auth = new KeycloakAuth(ssoUrl, ssoRealm, ssoClientId, ssoClientSecret);

        return new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
    }
}
