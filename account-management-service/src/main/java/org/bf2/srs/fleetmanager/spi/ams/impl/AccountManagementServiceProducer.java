package org.bf2.srs.fleetmanager.spi.ams.impl;

import io.apicurio.rest.client.JdkHttpClientProvider;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import io.quarkus.arc.profile.UnlessBuildProfile;
import org.bf2.srs.fleetmanager.spi.ams.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.ams.impl.exception.AccountManagementSystemAuthErrorHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;

@ApplicationScoped
public class AccountManagementServiceProducer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "account-management-system.url")
    String endpoint;

    @ConfigProperty(name = "account-management-system.local.enabled")
    boolean useLocalAms;

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

    @Produces
    @UnlessBuildProfile("test")
    public AccountManagementService produces() {
        if (useLocalAms) {
            log.info("Using Local Account Management Service.");
            return new LocalAccountManagementService();
        } else {
            log.info("Using Remote Account Management Service with Account Management URL: {}", endpoint);
            return new AccountManagementServiceImpl(amsProperties, createAccountManagementRestClient());
        }
    }

    private AccountManagementSystemRestClient createAccountManagementRestClient() {
        AccountManagementSystemRestClient restClient;
        if (ssoEnabled) {
            ApicurioHttpClient httpClient = new JdkHttpClientProvider().create(ssoTokenEndpoint, Collections.emptyMap(), null, new AccountManagementSystemAuthErrorHandler());
            final OidcAuth auth = new OidcAuth(httpClient, ssoClientId, ssoClientSecret);
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), auth);
        } else {
            restClient = new AccountManagementSystemRestClient(endpoint, Collections.emptyMap(), null);
        }
        return restClient;
    }
}
