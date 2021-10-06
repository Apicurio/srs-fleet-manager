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

package org.bf2.srs.fleetmanager.it;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

import java.util.List;

import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Registry;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreate;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryList;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;

/**
 * @author Fabian Martinez
 */
public class FleetManagerApi {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    public static void verifyApiIsSecured() {
        given()
            .log().all()
            .when().get(BASE)
            .then().statusCode(HTTP_UNAUTHORIZED);
    }

    public static Registry createRegistry(RegistryCreate registry, AccountInfo user) {
        return given()
                .log().all()
                .auth().oauth2(getAccessToken(user))
                .when().contentType(ContentType.JSON).body(registry)
                .post(BASE)
                .then().statusCode(HTTP_OK)
                .extract().as(Registry.class);
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
}
