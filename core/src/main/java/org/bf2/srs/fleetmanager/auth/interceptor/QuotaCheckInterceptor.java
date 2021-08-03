package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.spi.AccountManagementService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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

    @ConfigProperty(name = "srs-fleet-manager.registry.product-id")
    String productId;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        boolean allowed = true;
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            allowed = accountManagementService.hasEntitlements(accountInfo, "cluster.aws", "", productId);
        }
        if (allowed) {
            return context.proceed();
        }
        throw new ForbiddenException();
    }
}
