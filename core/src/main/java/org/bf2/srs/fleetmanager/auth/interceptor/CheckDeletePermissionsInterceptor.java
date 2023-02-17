package org.bf2.srs.fleetmanager.auth.interceptor;

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.auth.NotAuthorizedException;
import org.bf2.srs.fleetmanager.common.storage.ResourceStorage;
import org.bf2.srs.fleetmanager.common.storage.model.RegistryData;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingEvent;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingService;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import static org.bf2.srs.fleetmanager.util.SecurityUtil.*;

@CheckDeletePermissions
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class CheckDeletePermissionsInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    Instance<SecurityIdentity> securityIdentity;

    @Inject
    AuthService authService;

    @Inject
    ResourceStorage storage;

    @Inject
    AuditingService audit;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        if (isResolvable(securityIdentity)) {
            final AccountInfo accountInfo = authService.extractAccountInfo();
            final Optional<RegistryData> registry = storage.getRegistryById(context.getParameters()[0].toString());
            if (userCanDeleteInstance(accountInfo, registry)) {
                return context.proceed();
            }
        } else {
            return context.proceed();
        }
        log.info("Attempt to delete registry instance without the proper permissions");
        var ae = new AuditingEvent();
        ae.setEventId("authorization_failure");
        ae.addData("target", "registry");
        ae.addData("operation", "delete");
        audit.recordEvent(ae);
        throw new NotAuthorizedException(); // TODO Enable auditing for exceptions?
    }

    private static boolean userCanDeleteInstance(AccountInfo accountInfo, Optional<RegistryData> registry) {
        if (null == accountInfo.getAccountId()) {
            throw new IllegalStateException("Account id cannot be null in the jwt");
        } else {
            return registry.isEmpty() || isInstanceOwner(accountInfo, registry.get().getOwnerId()) || isOrgAdmin(accountInfo, registry.get().getOrgId());
        }
    }

}
