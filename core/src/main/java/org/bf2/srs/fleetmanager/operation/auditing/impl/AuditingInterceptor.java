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

import io.quarkus.security.identity.SecurityIdentity;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingEvent;
import org.bf2.srs.fleetmanager.operation.auditing.AuditingService;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryCreateDto;
import org.bf2.srs.fleetmanager.rest.service.model.RegistryDto;
import org.bf2.srs.fleetmanager.spi.model.CreateTenantRequest;
import org.bf2.srs.fleetmanager.spi.model.ResourceType;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.UpdateTenantRequest;

import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import static java.util.Map.entry;
import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.*;
import static org.bf2.srs.fleetmanager.common.util.StringUtil.shorten;

/**
 * Interceptor that executes around methods annotated with {@link org.bf2.srs.fleetmanager.common.operation.auditing.Audited}
 *
 * @author Jakub Senko <jsenko@redhat.com>
 */
@Audited
@Interceptor
@Priority(Interceptor.Priority.APPLICATION - 100)
// Runs before other application interceptors, e.g. *PermissionInterceptor
public class AuditingInterceptor {

    @Inject
    AuditingService auditing;

    @Inject
    SecurityIdentity securityIdentity;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {

        Audited annotation = context.getMethod().getAnnotation(Audited.class);
        if (annotation.extractParameters().length % 2 != 0)
            throw new IllegalStateException("Field @Audited.extractParameters on method '" +
                    context.getMethod().getName() + "' must contain an even number of elements.");

        var event = new AuditingEvent();

        if (securityIdentity != null && !securityIdentity.isAnonymous()) {
            event.addData("principalId", securityIdentity.getPrincipal().getName());
        }

        // Event ID
        var eventId = annotation.eventId();
        if (eventId.isBlank()) {
            eventId = EVENT_ID_METHOD_CALL_PREFIX + context.getMethod().getName();
        }
        event.setEventId(eventId);
        event.addData(KEY_CLASS, context.getTarget().getClass().getCanonicalName());

        // Event Description
        var eventDescription = annotation.eventDescription();
        if (!eventDescription.isBlank()) {
            event.setEventDescription(eventDescription);
        }

        // Parameter extraction via annotation
        var annotationParams = annotation.extractParameters();
        if (annotationParams.length > 0) {
            for (int i = 0; i <= annotationParams.length - 2; i += 2) {
                var key = annotationParams[i + 1];
                var value = context.getParameters()[Integer.parseInt(annotationParams[i])];
                event.addData(key, value);
            }
        }

        // Parameter extraction via extractors
        for (Object param : context.getParameters()) {
            if(param != null) {
                var extractor = PARAMETER_EXTRACTORS.get(param.getClass());
                if (extractor != null) {
                    extractor.accept(param, event);
                }
            }
        }

        try {
            var result = context.proceed();
            event.setSuccessful(true);
            if (result != null) {
                // Return value extraction via annotation
                if (!annotation.extractResult().isBlank()) {
                    var key = annotation.extractResult();
                    event.addData(key, result);
                }
                // Return value extraction via extractors
                var extractor = PARAMETER_EXTRACTORS.get(result.getClass());
                if (extractor != null) {
                    extractor.accept(result, event);
                }
            }
            return result;
        } catch (Exception ex) {
            event.setSuccessful(false);
            var message = ex.getClass().getCanonicalName() +
                    (ex.getMessage() != null ? ": " + ex.getMessage() : "");
            event.addData(KEY_ERROR_MESSAGE, shorten(message, 120));
            throw ex;
        } finally {
            auditing.recordEvent(event);
        }
    }

    private static final Map<Class<?>, BiConsumer<Object, AuditingEvent>> PARAMETER_EXTRACTORS = Map.ofEntries(
            entry(RegistryCreateDto.class, (obj, event) -> {
                var data = (RegistryCreateDto) obj;
                event.addData(KEY_REGISTRY_NAME, data.getName());
            }),
            entry(RegistryDto.class, (obj, event) -> {
                var data = (RegistryDto) obj;
                event.addData(KEY_REGISTRY_ID, data.getId());
                event.addData(KEY_REGISTRY_NAME, data.getName());
                event.addData(KEY_REGISTRY_INSTANCE_TYPE, data.getInstanceType());
                event.addData(KEY_REGISTRY_ORG_ID, data.getOrgId());
                event.addData(KEY_REGISTRY_OWNER, data.getOwner());
                event.addData(KEY_REGISTRY_OWNER_ID, data.getOwnerId());
                event.addData(KEY_REGISTRY_SUBSCRIPTION_ID, data.getSubscriptionId());
            }),
            entry(ResourceType.class, (obj, event) -> {
                var data = (ResourceType) obj;
                event.addData(KEY_AMS_RESOURCE_TYPE, data);
            }),
            entry(TenantManagerConfig.class, (obj, event) -> {
                var data = (TenantManagerConfig) obj;
                event.addData(KEY_TENANT_MANAGER_URL, data.getTenantManagerUrl());
                event.addData(KEY_DEPLOYMENT_URL, data.getRegistryDeploymentUrl());
            }),
            entry(CreateTenantRequest.class, (obj, event) -> {
                var data = (CreateTenantRequest) obj;
                event.addData(KEY_TENANT_ID, data.getTenantId());
                event.addData(KEY_TENANT_ORG_ID, data.getOrganizationId());
                event.addData(KEY_TENANT_USER, data.getCreatedBy());
            }),
            entry(UpdateTenantRequest.class, (obj, event) -> {
                var data = (UpdateTenantRequest) obj;
                event.addData(KEY_TENANT_ID, data.getId());
            })
            /*
             * TODO Additional parameter or return value types that can be used for extraction:
             *
             * ErrorDto
             * RegistryDeploymentCreate
             * RegistryDeployment
             * RegistryDto
             * RegistryCreateDto
             * ServiceStatusDto
             * Task
             * RegistryDeployment
             * Tenant
             *
             * + Collections, which would require something like
             * https://guava.dev/releases/23.0/api/docs/com/google/common/reflect/TypeToken.html
             */
    );
}
