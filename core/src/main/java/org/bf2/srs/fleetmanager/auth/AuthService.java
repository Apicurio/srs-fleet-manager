package org.bf2.srs.fleetmanager.auth;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ConfigProperty(name = "srs-fleet-manager.organization-id.claim-name")
    String organizationIdClaimName;

    @ConfigProperty(name = "srs-fleet-manager.auth.enabled")
    boolean authEnabled;

    @ConfigProperty(name = "srs-fleet-manager.default-org")
    String defaultOrg;

    @ConfigProperty(name = "srs-fleet-manager.is-org-admin.claim-name")
    String isAdminClaim;

    @Inject
    Instance<JsonWebToken> jwt;

    public AccountInfo extractAccountInfo() {
        if (isTokenResolvable()) {
            log.debug("Extracting account information from the authentication token");
            final String username = jwt.get().getName();
            final String organizationId = (String) jwt.get().claim(organizationIdClaimName).orElse(defaultOrg);
            boolean isOrgAdmin = false;
            final Optional<Object> isOrgAdminClaimValue = jwt.get().claim(isAdminClaim);

            if (isOrgAdminClaimValue.isPresent()) {
                isOrgAdmin = Boolean.valueOf(isOrgAdminClaimValue.get().toString());
            }
            return new AccountInfo(organizationId, username, isOrgAdmin);
        }
        return null;
    }

    private boolean isTokenResolvable() {
        return authEnabled && jwt.isResolvable() && jwt.get().getRawToken() != null;
    }
}
