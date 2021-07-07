package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.storage.RegistryNotFoundException;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;
import org.bf2.srs.fleetmanager.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;

import java.util.Optional;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

@CheckReadPermissions
@Interceptor
public class CheckReadPermissionsInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @Inject
    ResourceStorage storage;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            final Optional<RegistryData> registry = storage.getRegistryById(Long.parseLong(context.getParameters()[0].toString()));
            if (userCanReadInstance(accountInfo, registry)) {
                return context.proceed();
            }
        } else {
            return context.proceed();
        }
        log.info("Attempt to read registry instance without the proper permissions");
        throw new ForbiddenException();
    }

    private static boolean userCanReadInstance(AccountInfo accountInfo, Optional<RegistryData> registry) throws RegistryNotFoundException {
        if (null == accountInfo.getAccountId()) {
            throw new IllegalStateException("Account id cannot be null in the jwt");
        } else if (registry.isPresent()) {
            if (accountInfo.getOrganizationId() != null) {
                if (accountInfo.getOrganizationId().equals(registry.get().getOrgId())) {
                    return true;
                } else {
                    //throw not found exception to avoid leaking information of other registries from users in other organizations
                    throw new RegistryNotFoundException();
                }
            } else {
                return SecurityUtil.isInstanceOwner(accountInfo, registry.get().getOwnerId());
            }
        } else {
            return true;
        }
    }

}
