package io.cloudevents.examples.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;

@QuarkusTest
public class UserResourceTest {

    @Test
    public void testUserEndpoint() {
        given()
          .when().get("/users")
          .then()
             .statusCode(200);
    }

}
