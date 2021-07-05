package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

@CheckDeletePermission
@Interceptor
public class CheckDeleteInterceptor {

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        boolean allowed = true;
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            allowed = accountInfo.isAdmin();
        }
        if (allowed) {
            return context.proceed();
        }
        throw new ForbiddenException();
    }
}
