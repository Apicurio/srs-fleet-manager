package org.bf2.srs.fleetmanager.util;

import io.quarkus.security.identity.SecurityIdentity;

import javax.enterprise.inject.Instance;

import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

public class SecurityUtil {

    public static final String OWNER_PLACEHOLDER = "Unauthenticated";
    //TODO change this to -1 ?
    public static final Long OWNER_ID_PLACEHOLDER = 1L;

    private SecurityUtil() {
        //utility class
    }

    public static boolean isResolvable(Instance<SecurityIdentity> securityIdentity) {
        return securityIdentity.isResolvable() && !securityIdentity.get().isAnonymous();
    }

    public static boolean isInstanceOwner(AccountInfo accountInfo, Long ownerId) {
        return accountInfo.getAccountId().equals(ownerId);
    }

    public static boolean isOrgAdmin(AccountInfo accountInfo, String registryInstanceOrg) {
        return accountInfo.isAdmin() && accountInfo.getOrganizationId().equals(registryInstanceOrg);
    }
}
