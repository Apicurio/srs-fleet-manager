package org.bf2.srs.fleetmanager.util;

import io.quarkus.security.identity.SecurityIdentity;

import javax.enterprise.inject.Instance;

public class SecurityUtil {

    public static boolean isResolvable(Instance<SecurityIdentity> securityIdentity) {

        return securityIdentity.isResolvable() && !securityIdentity.get().isAnonymous();
    }
}
