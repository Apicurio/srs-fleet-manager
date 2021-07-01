package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.isResolvable;

@CheckQuota
@Interceptor
public class QuotaCheckInterceptor {

    @Inject
    AccountManagementService accountManagementService;

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        boolean allowed = true;
        if (isResolvable(securityIdentity)) {
            //TODO fill resoure type and cluster id
            final AccountInfo accountInfo = authService.extractAccountInfo();
            allowed = accountManagementService.hasEntitlements(accountInfo, "", "");
        }
        if (allowed) {
            return context.proceed();
        }
        throw new ForbiddenException();
    }
}
