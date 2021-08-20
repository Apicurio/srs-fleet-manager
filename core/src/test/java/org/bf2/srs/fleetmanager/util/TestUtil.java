package org.bf2.srs.fleetmanager.util;

import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValueRest;
import org.bf2.srs.fleetmanager.spi.TenantManagerService;
import org.bf2.srs.fleetmanager.spi.model.TenantManagerConfig;
import org.bf2.srs.fleetmanager.spi.model.TenantStatus;
import org.bf2.srs.fleetmanager.spi.model.UpdateTenantRequest;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static java.lang.System.currentTimeMillis;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bf2.srs.fleetmanager.rest.RegistriesResourceV1Test.BASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    public static void waitForDeletion(TenantManagerService tms, TenantManagerConfig tmc, List<RegistryRest> registries) {

        Awaitility.await("Registry deleting initiated").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> registries.stream().allMatch(r -> {
                    var tenant = tms.getTenantById(tmc, TestUtil.getTenantIdFromUrl(r.getRegistryUrl()));
                    return TenantStatus.TO_BE_DELETED.equals(tenant.get().getStatus());
                }));

        registries.forEach(r -> {
            var req = UpdateTenantRequest.builder()
                    .id(TestUtil.getTenantIdFromUrl(r.getRegistryUrl()))
                    .status(TenantStatus.DELETED)
                    .build();
            tms.updateTenant(tmc, req);
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

    public static List<RegistryRest> waitForReady(List<RegistryRest> registries) {
        Awaitility.await("Registry ready").atMost(5, SECONDS).pollInterval(1, SECONDS)
                .until(() -> registries.stream().allMatch(r -> {
                    var reg = given().log().all()
                            .when().get(BASE + "/" + r.getId())
                            .then().statusCode(HTTP_OK)
                            .extract().as(RegistryRest.class);
                    return RegistryStatusValueRest.ready.equals(reg.getStatus());
                }));
        return registries.stream().map(r -> given().log().all()
                .when().get(BASE + "/" + r.getId())
                .then().statusCode(HTTP_OK)
                .extract().as(RegistryRest.class)).collect(Collectors.toList());
    }

    public static String getTenantIdFromUrl(String registryUrl) {
        assertNotNull(registryUrl);
        var tokens = registryUrl.split("/t/");
        assertEquals(2, tokens.length);
        return tokens[1];
    }
}
