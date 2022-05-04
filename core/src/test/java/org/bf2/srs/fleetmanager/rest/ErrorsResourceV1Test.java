package org.bf2.srs.fleetmanager.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.Error;
import org.bf2.srs.fleetmanager.rest.publicapi.beans.ErrorList;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@QuarkusTest
public class ErrorsResourceV1Test {

    public static final String BASE = "/api/serviceregistry_mgmt/v1/errors";

    @Test
    void testGetError() {

        UserErrorCode.getValueMap().entrySet().stream().forEach(e -> {
            var error = given().log().all()
                    .when().contentType(ContentType.JSON).get(BASE + "/{id}", e.getKey())
                    .then().statusCode(HTTP_OK)
                    .extract().as(Error.class);

            assertEquals(e.getKey().toString(), error.getId());
            assertEquals("Error", error.getKind());
            assertEquals("/api/serviceregistry_mgmt/v1/errors/" + e.getKey(), error.getHref());
            assertEquals(e.getValue().getCode(), error.getCode());
            assertEquals(e.getValue().getReasonArgsCount(), error.getReason().chars().filter(ch -> ch == '?').count());
        });

        var error = given().log().all()
                .when().contentType(ContentType.JSON).get(BASE + "/{id}", UserErrorCode.getValueMap().size() + 1)
                .then().statusCode(HTTP_NOT_FOUND)
                .extract().as(Error.class);

        assertEquals(Integer.toString(UserErrorCode.ERROR_ERROR_TYPE_NOT_FOUND.getId()), error.getId());
    }

    @Test
    void testGetErrors() {

        var errorList = given().log().all()
                .when().contentType(ContentType.JSON).get(BASE)
                .then().statusCode(HTTP_OK)
                .extract().as(ErrorList.class);

        errorList = given().log().all()
                .when().contentType(ContentType.JSON).get(BASE + "?page={page}&size={size}", 1, UserErrorCode.getValueMap().size() * 2)
                .then().statusCode(HTTP_OK)
                .extract().as(ErrorList.class);

        assertEquals("ErrorList", errorList.getKind());
        assertEquals(1, errorList.getPage());
        assertEquals(UserErrorCode.getValueMap().size() * 2, errorList.getSize());
        assertEquals(UserErrorCode.getValueMap().size(), errorList.getTotal());
        assertEquals(UserErrorCode.getValueMap().size(), errorList.getItems().size());

        errorList = given().log().all()
                .when().contentType(ContentType.JSON).get(BASE + "?page={page}&size={size}", 3, 1)
                .then().statusCode(HTTP_OK)
                .extract().as(ErrorList.class);

        assertEquals("ErrorList", errorList.getKind());
        assertEquals(3, errorList.getPage());
        assertEquals(1, errorList.getSize());
        assertEquals(UserErrorCode.getValueMap().size(), errorList.getTotal());
        assertEquals(1, errorList.getItems().size());

        var error = errorList.getItems().get(0);
        // Error Code = 3
        assertEquals(Integer.toString(UserErrorCode.ERROR_FORMAT_DATETIME.getId()), error.getId());
    }
}