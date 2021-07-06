package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.bf2.srs.fleetmanager.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.storage.sqlPanacheImpl.model.RegistryData;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;

import java.util.Optional;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

@CheckDeletePermissions
@Interceptor
public class CheckDeletePermissionsInterceptor {

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
            if (isAdminOrOwner(accountInfo, registry)) {
                return context.proceed();
            }
        } else {
            return context.proceed();
        }
        throw new ForbiddenException();
    }

    public boolean isAdminOrOwner(AccountInfo accountInfo, Optional<RegistryData> registry) {
        return accountInfo.isAdmin() || registry.isEmpty() || accountInfo.getAccountUsername().equals(registry.get().getOwner());
    }
}
