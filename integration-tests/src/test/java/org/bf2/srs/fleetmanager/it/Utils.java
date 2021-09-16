package org.bf2.srs.fleetmanager.it;

import io.apicurio.multitenant.api.datamodel.TenantStatusValue;
import io.apicurio.multitenant.api.datamodel.UpdateRegistryTenantRequest;
import io.apicurio.multitenant.client.TenantManagerClient;
import io.apicurio.multitenant.client.TenantManagerClientImpl;
import io.apicurio.rest.client.auth.OidcAuth;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

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
            var req = new UpdateRegistryTenantRequest();
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
        var infra = TestInfraManager.getInstance();
        if (infra.isTenantManagerAuthEnabled()) {
            var tmAuth = infra.getTenantManagerAuthConfig();
            OidcAuth auth = new OidcAuth(tmAuth.tokenEndpoint, tmAuth.clientId, tmAuth.clientSecret, Optional.empty());
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
            return new TenantManagerClientImpl(infra.getTenantManagerUri(), Collections.emptyMap(), auth);
        } else {
            return new TenantManagerClientImpl(infra.getTenantManagerUri());
        }
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
