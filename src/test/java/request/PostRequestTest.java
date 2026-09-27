package request;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Epic("FakeStore API Testing")
@Feature("POST Requests")
@Severity(SeverityLevel.CRITICAL)
@Story("Create Product")
public class PostRequestTest extends BaseTest {

    @Test
    @DisplayName("Create a new product and validate the response")
    public void createProduct() {

        String requestBody = "{\n" +
                "  \"title\": \"Test Product\",\n" +
                "  \"price\": 59.99,\n" +
                "  \"description\": \"Brand new test product\",\n" +
                "  \"image\": \"https://i.pravatar.cc\",\n" +
                "  \"category\": \"electronics\"\n" +
                "}";

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/products")
                .then()
                .statusCode(200)
                .body("title", equalTo("Test Product"))
                .body("price", equalTo(59.99f))
                .body("category", equalTo("electronics"));
    }
}
