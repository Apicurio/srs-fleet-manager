/*
 * Copyright 2021 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.srs.fleetmanager.operation.auditing.impl;

import static org.bf2.srs.fleetmanager.AuditingServletFilter.HEADER_X_FORWARDED_FOR;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_ERROR_MESSAGE;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_REQUEST_FORWARDED_FOR;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_REQUEST_METHOD;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_REQUEST_PATH;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_REQUEST_SOURCE_IP;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_RESPONSE_CODE;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.bf2.srs.fleetmanager.operation.auditing.AuditingEvent;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.oidc.runtime.BearerAuthenticationMechanism;
import io.quarkus.oidc.runtime.OidcAuthenticationMechanism;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

/**
 * Custom HttpAuthenticationMechanism that simply wraps OidcAuthenticationMechanism.
 * The only purpose of this HttpAuthenticationMechanism is to handle authentication errors in order to generate audit logs.
 *
 * @author Fabian Martinez
 * @author Jakub Senko <jsenko@redhat.com>
 */
@UnlessBuildProfile("test") // OIDC auth is disabled during testing (%test.quarkus.oidc.enabled=false)
@Alternative
@Priority(1)
@ApplicationScoped
public class AuditingAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    OidcAuthenticationMechanism oidcAuthenticationMechanism;

    private final BearerAuthenticationMechanism bearerAuth = new BearerAuthenticationMechanism();

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {

        BiConsumer<RoutingContext, Throwable> failureHandler = context.get(QuarkusHttpUser.AUTH_FAILURE_HANDLER);
        BiConsumer<RoutingContext, Throwable> auditWrapper = (ctx, ex) -> {
            //this sends the http response
            failureHandler.accept(ctx, ex);
            //if it was an error response log it
            if (ctx.response().getStatusCode() >= 400) {

                var event = new AuditingEvent();
                event.setEventId("authentication_failure");
                event.addData(KEY_REQUEST_SOURCE_IP, ctx.request().remoteAddress());
                event.addData(KEY_REQUEST_FORWARDED_FOR, ctx.request().getHeader(HEADER_X_FORWARDED_FOR));
                event.addData(KEY_REQUEST_METHOD, ctx.request().method().name());
                event.addData(KEY_REQUEST_PATH, ctx.request().path());
                event.addData(KEY_RESPONSE_CODE, ctx.response().getStatusCode());
                event.setSuccessful(false);

                if (ex != null) {
                    event.addData(KEY_ERROR_MESSAGE, ex.getMessage());
                }

                // Request Context does not exist at this point
                AuditingServiceImpl.recordEventNoContext(event);
            }
        };
        context.put(QuarkusHttpUser.AUTH_FAILURE_HANDLER, auditWrapper);

        return oidcAuthenticationMechanism.authenticate(context, identityProviderManager);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return bearerAuth.getChallenge(context);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.singleton(TokenAuthenticationRequest.class);
    }

    @Override
    public HttpCredentialTransport getCredentialTransport() {
        return new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, "bearer");
    }
}
