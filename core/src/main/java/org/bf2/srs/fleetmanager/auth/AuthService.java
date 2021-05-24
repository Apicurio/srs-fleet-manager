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

    @Inject
    Instance<JsonWebToken> jwt;

    public String extractOrganizationId() {
        if (jwt.isResolvable()) {
            log.debug("Extracting organization id from the authentication token");

            return (String) jwt.get().claim(organizationIdClaimName)
                    .orElse("");
        }
        return null;
    }
}
