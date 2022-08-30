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

package org.bf2.srs.fleetmanager.it.util;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import org.awaitility.Awaitility;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryStatusValue;
import org.bf2.srs.fleetmanager.spi.common.model.AccountInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Fabian Martinez
 */
public class FleetManagerApi {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";
    private static final String BASE_ADMIN = "/api/serviceregistry_mgmt/v1/admin";

    static {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new ErrorLoggingFilter());
    }

    public static void verifyApiIsSecured() {
        given()
                .log().all()
                .when().get(BASE)
                .then().statusCode(HTTP_UNAUTHORIZED);
    }

    public static <T> T createRegistry(RegistryCreate registry, AccountInfo user, int expectedStatusCode, Class<T> resultType) {
        return given()
                //.log().all()
                .auth().oauth2(getAccessToken(user))
                .when().contentType(ContentType.JSON).body(registry)
                .post(BASE)
                .then().statusCode(expectedStatusCode)
                .extract().as(resultType);
    }

    public static Registry createRegistry(RegistryCreate registry, AccountInfo user) {
        return createRegistry(registry, user, HTTP_OK, Registry.class);
    }

    public static List<Registry> listRegistries(AccountInfo user) {
        return given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE)
                .then().statusCode(HTTP_OK)
                .extract().as(RegistryList.class).getItems();
    }

    public static Registry getRegistry(String id, AccountInfo user) {
        return given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE + "/" + id)
                .then().statusCode(HTTP_OK)
                .extract().as(Registry.class);
    }

    public static void verifyRegistryNotExists(String id, AccountInfo user) {
        given().log().all()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE + "/" + id)
                .then().statusCode(HTTP_NOT_FOUND);
    }

    public static void deleteRegistry(String id, AccountInfo user) {
        given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().delete(BASE + "/" + id)
                .then().statusCode(HTTP_NO_CONTENT);
    }

    public static void verifyDeleteNotAllowed(String id, AccountInfo user) {
        given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().delete(BASE + "/" + id)
                .then().statusCode(HTTP_FORBIDDEN);
    }

    public static void verifyCreateDeploymentNotAllowed(RegistryDeploymentCreateRest deployment, AccountInfo user) {
        given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
                .then().statusCode(HTTP_FORBIDDEN);
    }

    private static String getAccessToken(AccountInfo account) {
        return Jwt.preferredUserName(account.getAccountUsername())

                .claim("org_id", account.getOrganizationId())
                .claim("account_id", String.valueOf(account.getAccountId()))
                .claim("is_org_admin", account.isAdmin())

                .jws()
                .keyId("1")
                .sign();
    }

    public static Registry waitRegistryReady(Registry registry, AccountInfo user) {
        assertNotEquals(RegistryStatusValue.failed, registry.getStatus());

        Awaitility.await("registry available").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .until(() -> {
                    var reg = FleetManagerApi.getRegistry(registry.getId(), user);
                    return reg.getStatus().equals(RegistryStatusValue.ready);
                });

        return FleetManagerApi.getRegistry(registry.getId(), user);
    }

    public static void waitRegistryDeleted(Registry registry, AccountInfo user) {
        assertNotEquals(RegistryStatusValue.failed, registry.getStatus());

        Awaitility.await("registry deleted").atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS)
                .untilAsserted(() -> FleetManagerApi.verifyRegistryNotExists(registry.getId(), user));
    }

    public static List<RegistryDeploymentRest> listRegistryDeployments(AccountInfo user) {
        return given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE_ADMIN + "/registryDeployments")
                .then().statusCode(HTTP_OK)
                .extract().as(new TypeRef<List<RegistryDeploymentRest>>() {
                });
    }

    public static void deleteRegistryDeployment(AccountInfo user, Integer deploymentId) {
        deleteRegistryDeployment(user, deploymentId, HTTP_NO_CONTENT);
    }

    public static void deleteRegistryDeployment(AccountInfo user, Integer deploymentId, int expectedCode) {
        given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().delete(BASE_ADMIN + "/registryDeployments/" + deploymentId)
                .then().statusCode(expectedCode);
    }

    public static RegistryDeploymentRest createRegistryDeployment(AccountInfo user, RegistryDeploymentCreateRest deployment) {
        return given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when()
                .contentType(ContentType.JSON)
                .body(deployment)
                .post(BASE_ADMIN + "/registryDeployments")
                .then().statusCode(HTTP_OK)
                .extract().as(RegistryDeploymentRest.class);
    }
}
