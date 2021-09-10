package org.bf2.srs.fleetmanager.spi.impl;

import io.apicurio.rest.client.auth.OidcAuth;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class AccountManagementServiceProducer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "account-management-system.url")
    String endpoint;

    @ConfigProperty(name = "sso.token.endpoint")
    String ssoTokenEndpoint;

    @ConfigProperty(name = "sso.client-id")
    String ssoClientId;

    @ConfigProperty(name = "sso.client-secret")
    String ssoClientSecret;

    @ConfigProperty(name = "sso.enabled")
    boolean ssoEnabled;

    @Inject
    AccountManagementServiceProperties amsProperties;

    @UnlessBuildProfile("test")
    @Produces
    @ApplicationScoped
    public AccountManagementService produce() {
        logger.info("Using Account Management Service with Account Management URL: {}", endpoint);
        AccountManagementSystemRestClient restClient;
        if (ssoEnabled) {
            final OidcAuth auth = new OidcAuth(ssoTokenEndpoint, ssoClientId, ssoClientSecret, Optional.of(new AccountManagementSystemErrorHandler()));
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
        } else {
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), null);
        }
        return new AccountManagementServiceImpl(restClient, amsProperties);
    }
}
