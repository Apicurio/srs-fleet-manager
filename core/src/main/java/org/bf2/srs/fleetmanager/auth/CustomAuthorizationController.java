package org.bf2.srs.fleetmanager.auth;

import io.quarkus.security.spi.runtime.AuthorizationController;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

@Alternative
@Priority(Interceptor.Priority.LIBRARY_AFTER)
@ApplicationScoped
public class CustomAuthorizationController extends AuthorizationController {

    @ConfigProperty(name = "srs-fleet-manager.auth.enabled")
    boolean enableAuthorization;

    @Override
    public boolean isAuthorizationEnabled() {
        return enableAuthorization;
    }
}