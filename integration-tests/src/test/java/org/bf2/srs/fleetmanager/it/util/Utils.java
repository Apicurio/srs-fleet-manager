package org.bf2.srs.fleetmanager.it.util;

import io.apicurio.tenantmanager.api.datamodel.TenantStatusValue;
import io.apicurio.tenantmanager.api.datamodel.UpdateApicurioTenantRequest;
import io.apicurio.tenantmanager.client.TenantManagerClient;
import io.apicurio.tenantmanager.client.TenantManagerClientImpl;
import io.apicurio.rest.client.JdkHttpClientProvider;
import io.apicurio.rest.client.auth.OidcAuth;
import io.apicurio.rest.client.auth.exception.AuthErrorHandler;
import io.apicurio.rest.client.spi.ApicurioHttpClient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.it.component.TenantManagerComponent;
import org.bf2.srs.fleetmanager.it.infra.InfraHolder;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;

import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bf2.srs.fleetmanager.it.component.CompoundComponent.C_TM;

public class Utils {

    /**
     * Since tenant deletion is asynchronous and needs to be partially mocked, use this convenience method.
     */
    public static void waitForDeletion(TenantManagerClient tmc, List<TenantInfo> tenants) {

        Awaitility.await("Registry deleting initiated").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> tenants.stream().allMatch(t -> {
                    var tenant = tmc.getTenant(t.getTenantId());
                    return TenantStatusValue.TO_BE_DELETED.equals(tenant.getStatus());
                }));

        tenants.forEach(t -> {
            var req = new UpdateApicurioTenantRequest();
            req.setStatus(TenantStatusValue.DELETED);
            tmc.updateTenant(t.getTenantId(), req);
        });

        Awaitility.await("Registry deleted").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> tenants.stream().allMatch(t -> {
                    try {
                        FleetManagerApi.verifyRegistryNotExists(t.getRegistryId(), t.getAccountInfo());
                        return true;
                    } catch (AssertionError ex) {
                        return false;
                    }
                }));
    }

    public static Registry waitForReady(Registry tenant, AccountInfo accountInfo) {
        Awaitility.await("Registry ready").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(tenant.getId(), accountInfo);
                    return RegistryStatusValue.ready.equals(reg.getStatus());
                });
        return FleetManagerApi.getRegistry(tenant.getId(), accountInfo);
    }

    public static TenantManagerClient createTenantManagerClient() {
        var infra = InfraHolder.getInstance().getComponent();
        var tm = infra.get(C_TM, TenantManagerComponent.class).get();

        if (tm.isAuthEnabled()) {
            var tmAuth = tm.getAuthConfig();
            ApicurioHttpClient httpClient = new JdkHttpClientProvider().create(tmAuth.getTokenEndpoint(), Collections.emptyMap(), null, new AuthErrorHandler());
            OidcAuth auth = new OidcAuth(httpClient, tmAuth.getClientId(), tmAuth.getClientSecret());
            // TODO uncomment
            // {
            //     //verify tenant manager auth
            //     var invalidTMClient = new TenantManagerClientImpl(infra.getTenantManagerUri(),
            //  new Auth(tmAuth.keycloakUrl, tmAuth.realm, "foo", "baz"));
            //     try {
            //         invalidTMClient.deleteTenant("foo");
            //         Assertions.fail("Tenant Manager auth is not working");
            //     } catch (TenantManagerClientException e) {
            //         if (!VerificationException.class.isInstance(e)) {
            //  Assertions.fail("Tenant Manager auth did not throw VerificationException");
            //         }
            //     }
            // }
            return new TenantManagerClientImpl(tm.getTenantManagerUrl(), Collections.emptyMap(), auth);
        } else {
            return new TenantManagerClientImpl(tm.getTenantManagerUrl());
        }
    }

    public static void simulateRegistryDeletion(TenantManagerClient tmc, Registry registry, AccountInfo user) {
        Awaitility.await("registry '" + registry + "' deleting initiated").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> {
                    var tenant1 = tmc.getTenant(registry.getId());
                    return TenantStatusValue.TO_BE_DELETED.equals(tenant1.getStatus());
                });

        var req = new UpdateApicurioTenantRequest();
        req.setStatus(TenantStatusValue.DELETED);
        tmc.updateTenant(registry.getId(), req);

        Awaitility.await("registry '" + registry + "' deleted").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> {
                    try {
                        FleetManagerApi.verifyRegistryNotExists(registry.getId(), user);
                        return true;
                    } catch (AssertionError ex) {
                        return false;
                    }
                });
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    public static class TenantInfo {

        @Getter
        private final String tenantId;

        @Getter
        private final String registryId;

        @Getter
        private final AccountInfo accountInfo;
    }
}
