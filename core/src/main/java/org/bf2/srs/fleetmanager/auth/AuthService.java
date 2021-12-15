package org.bf2.srs.fleetmanager.auth;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.util.SecurityUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Optional;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.OWNER_ID_PLACEHOLDER;
import static org.bf2.srs.fleetmanager.util.SecurityUtil.OWNER_PLACEHOLDER;

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

    @ConfigProperty(name = "srs-fleet-manager.account-id.claim-name")
    String accountIdClaim;

    @ConfigProperty(name = "srs-fleet-manager.default-account-id")
    String defaultAccountId;

    @Inject
    Instance<JsonWebToken> jwt;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    public AccountInfo extractAccountInfo() {
        AccountInfo accountInfo = new AccountInfo(defaultOrg, OWNER_PLACEHOLDER, false, OWNER_ID_PLACEHOLDER);
        if (SecurityUtil.isResolvable(securityIdentity)) {
            if (isTokenResolvable()) {
                log.trace("Extracting account information from the authentication token");
                final String username = jwt.get().getName();
                final String organizationId = (String) jwt.get().claim(organizationIdClaimName).orElse(defaultOrg);
                final Long accountId = Long.parseLong((String) jwt.get().claim(accountIdClaim).orElse(defaultAccountId));

                boolean isOrgAdmin = false;
                final Optional<Object> isOrgAdminClaimValue = jwt.get().claim(isAdminClaim);

                if (isOrgAdminClaimValue.isPresent()) {
                    isOrgAdmin = Boolean.valueOf(isOrgAdminClaimValue.get().toString());
                }
                return new AccountInfo(organizationId, username, isOrgAdmin, accountId);
            }
        }
        return accountInfo;
    }

    private boolean isTokenResolvable() {
        return authEnabled && jwt.isResolvable() && jwt.get().getRawToken() != null;
    }
}
