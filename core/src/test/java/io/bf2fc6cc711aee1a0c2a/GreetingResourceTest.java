package io.bf2fc6cc711aee1a0c2a;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    @Disabled
    public void testHelloEndpoint() {
        given()
                .when().get("/hello-resteasy")
                .then()
                .statusCode(200)
                .body(is("Hello RESTEasy"));
    }

}