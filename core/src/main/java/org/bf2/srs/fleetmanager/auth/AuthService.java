package org.bf2.srs.fleetmanager.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.organization-id.claim-name")
    String organizationIdClaimName;

    @ConfigProperty(name = "srs-fleet-manager.auth.enabled")
    boolean authEnabled;

    @ConfigProperty(name = "srs-fleet-manager.default-org")
    String defaultOrg;

    @Inject
    Instance<JsonWebToken> jwt;

    public String extractOrganizationId() {
        if (jwt.isResolvable() && authEnabled) {
            log.debug("Extracting organization id from the authentication token");

            return (String) jwt.get().claim(organizationIdClaimName)
                    .orElse("");
        } else {
            return defaultOrg;
        }
    }

    public AccountInfo extractAccountInfo() {

        final String username = securityIdentity.getAttribute(AuthKeys.USERNAME);
        final String organizationId = securityIdentity.getAttribute(AuthKeys.ORGANIZATION_ID);

        return new AccountInfo(organizationId, username);
    }
}
