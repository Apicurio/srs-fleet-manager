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
import static java.net.HttpURLConnection.*;

import java.util.List;

import org.bf2.srs.fleetmanager.rest.privateapi.beans.RegistryDeploymentCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryCreateRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryListRest;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.RegistryRest;
import org.bf2.srs.fleetmanager.spi.model.AccountInfo;

import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;

/**
 * @author Fabian Martinez
 */
public class FleetManagerApi {

    private static final String BASE = "/api/serviceregistry_mgmt/v1/registries";

    public void verifyApiIsSecured() {
        given()
            .when().get(BASE)
            .then().statusCode(HTTP_UNAUTHORIZED)
            .log().all();
    }

    public RegistryRest createRegistry(RegistryCreateRest registry, AccountInfo user) {
        return given()
                .auth().oauth2(getAccessToken(user))
                .when().contentType(ContentType.JSON).body(registry)
                .post(BASE)
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryRest.class);
    }

    public List<RegistryRest> listRegistries(AccountInfo user) {
        return given()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE)
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryListRest.class).getItems();
    }

    public RegistryRest getRegistry(String id, AccountInfo user) {
        return given()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE + "/" + id)
                .then().statusCode(HTTP_OK)
                .log().all()
                .extract().as(RegistryRest.class);
    }

    public void verifyRegistryNotExists(String id, AccountInfo user) {
        given()
                .auth().oauth2(getAccessToken(user))
                .when().get(BASE + "/" + id)
                .then().statusCode(HTTP_NOT_FOUND)
                .log().all();
    }

    public void deleteRegistry(String id, AccountInfo user) {
        given()
            .auth().oauth2(getAccessToken(user))
            .when().delete(BASE + "/" + id)
            .then().statusCode(HTTP_NO_CONTENT)
            .log().all();
    }

    public void verifyDeleteNotAllowed(String id, AccountInfo user) {
        given()
            .auth().oauth2(getAccessToken(user))
            .when().delete(BASE + "/" + id)
            .then().statusCode(HTTP_FORBIDDEN)
            .log().all();
    }

    public void verifyCreateDeploymentNotAllowed(RegistryDeploymentCreateRest deployment, AccountInfo user) {
        given()
            .auth().oauth2(getAccessToken(user))
            .when().contentType(ContentType.JSON).body(deployment).post("/api/serviceregistry_mgmt/v1/admin/registryDeployments")
            .then().statusCode(HTTP_FORBIDDEN)
            .log().all();
    }

    private String getAccessToken(AccountInfo account) {
        return Jwt.preferredUserName(account.getAccountUsername())

                .claim("org_id", account.getOrganizationId())
                .claim("account_id", String.valueOf(account.getAccountId()))
                .claim("is_org_admin", account.isAdmin())

                .jws()
                .keyId("1")
                .sign();
    }
}
