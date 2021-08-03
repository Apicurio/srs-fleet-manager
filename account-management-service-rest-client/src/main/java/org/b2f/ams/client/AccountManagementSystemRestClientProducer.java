package org.b2f.ams.client;

import io.apicurio.rest.client.auth.OidcAuth;
import io.quarkus.arc.profile.UnlessBuildProfile;
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

    @ConfigProperty(name = "sso.token.endpoint")
    private String ssoTokenEndpoint;

    @ConfigProperty(name = "sso.client-id")
    private String ssoClientId;

    @ConfigProperty(name = "sso.client-secret")
    private String ssoClientSecret;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementSystemRestClient produce() {
        log.info("Using Account Management System REST client.");

        final OidcAuth auth = new OidcAuth(ssoTokenEndpoint, ssoClientId, ssoClientSecret);

        return new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
    }
}
