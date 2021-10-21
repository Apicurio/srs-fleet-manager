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
package org.bf2.srs.fleetmanager;

import org.bf2.srs.fleetmanager.auth.AuthService;
import org.bf2.srs.fleetmanager.operation.OperationContext;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingEvent;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingService;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.*;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class MainServletFilter implements Filter {

    public static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";

    @Inject
    AuditingService auditing;

    @Inject
    OperationContext opCtx;

    @Inject
    AuthService authService;

    @Override
    @ActivateRequestContext
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Activate Operation Context
        if (opCtx.isContextDataLoaded())
            throw new IllegalStateException("Unexpected state: Operation Context is already loaded");
        opCtx.loadNewContextData();

        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        // TODO Unify logic to extract this using parameter extractors
        auditing.addTraceMetadata(KEY_REQUEST_SOURCE_IP, req.getRemoteAddr());
        auditing.addTraceMetadata(KEY_REQUEST_FORWARDED_FOR, req.getHeader(HEADER_X_FORWARDED_FOR));
        auditing.addTraceMetadata(KEY_REQUEST_METHOD, req.getMethod());
        auditing.addTraceMetadata(KEY_REQUEST_PATH, req.getRequestURI());

        AccountInfo accountInfo = authService.extractAccountInfo();

        auditing.addTraceMetadata(KEY_USER_ACCOUNT_ID, accountInfo.getAccountId());
        auditing.addTraceMetadata(KEY_USER_ACCOUNT_NAME, accountInfo.getAccountUsername());
        auditing.addTraceMetadata(KEY_USER_ORG_ID, accountInfo.getOrganizationId());
        auditing.addTraceMetadata(KEY_USER_IS_ORG_ADMIN, accountInfo.isAdmin());

        chain.doFilter(request, response);

        if (res.getStatus() >= 400) {

            var event = new AuditingEvent();
            event.setEventId("request_failure");
            event.addData(KEY_RESPONSE_CODE, res.getStatus());
            event.setSuccessful(false);

            auditing.recordEvent(event);
        }
    }
}
