package org.bf2.srs.fleetmanager.util;

import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.tenants.TenantManagerServiceException;
import org.bf2.srs.fleetmanager.spi.tenants.model.Tenant;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.tenants.model.TenantStatus;
import org.bf2.srs.fleetmanager.spi.tenants.model.UpdateTenantRequest;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bf2.srs.fleetmanager.rest.RegistriesResourceV1Test.BASE;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
public class TestUtil {

    public static void delay(long millis) {
        long diff = millis;
        long end = currentTimeMillis() + diff;
        while (diff > 0) {
            try {
                Thread.sleep(diff);
            } catch (InterruptedException e) {
                // NOOP
            }
            long now = currentTimeMillis();
            diff = end - now;
        }
    }

    public static void waitForDeletion(TenantManagerService tms, TenantManagerConfig tmc, List<Registry> registries) {

        Awaitility.await("Registry deleting initiated")
                .atMost(5, SECONDS)
                .pollInterval(1, SECONDS)
                .pollInSameThread() // To preserve the Operation Context for TenantManagerService execution.
                .until(() -> registries.stream().allMatch(r -> {
                    Optional<Tenant> tenant = null;
                    try {
                        tenant = tms.getTenantById(tmc, r.getId());
                    } catch (TenantManagerServiceException ex) {
                        Assertions.fail(ex);
                    }
                    return TenantStatus.TO_BE_DELETED.equals(tenant.get().getStatus());
                }));

        registries.forEach(r -> {
            var req = UpdateTenantRequest.builder()
                    .id(r.getId())
                    .status(TenantStatus.DELETED)
                    .build();
            try {
                tms.updateTenant(tmc, req);
            } catch (Exception ex) {
                Assertions.fail(ex);
            }
        });

        Awaitility.await("Registry deleted").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> registries.stream().allMatch(r -> {
                    try {
                        given().log().all()
                                .when().get(BASE + "/" + r.getId())
                                .then().statusCode(HTTP_NOT_FOUND);
                        return true;
                    } catch (AssertionError ex) {
                        return false;
                    }
                }));
    }

    public static List<Registry> waitForReady(List<Registry> registries) {
        Awaitility.await("Registry ready").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> registries.stream().allMatch(r -> {
                    var reg = given().log().all()
                            .when().get(BASE + "/" + r.getId())
                            .then().statusCode(HTTP_OK)
                            .extract().as(Registry.class);
                    return RegistryStatusValue.ready.equals(reg.getStatus());
                }));
        return registries.stream().map(r -> given().log().all()
                .when().get(BASE + "/" + r.getId())
                .then().statusCode(HTTP_OK)
                .extract().as(Registry.class)).collect(Collectors.toList());
    }
}
