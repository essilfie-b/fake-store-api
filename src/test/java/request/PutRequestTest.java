package request;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Epic("FakeStore API Testing")
@Feature("PUT Requests")
@Severity(SeverityLevel.CRITICAL)
@Story("Update Product")
public class PutRequestTest extends BaseTest {

    @Test
    @DisplayName("Update an existing product and validate the response")
    public void updateProduct() {

        int productId = 1;

        String requestBody = "{\n" +
                "  \"title\": \"Updated Product Title\",\n" +
                "  \"price\": 99.99,\n" +
                "  \"description\": \"Updated product description\",\n" +
                "  \"image\": \"https://i.pravatar.cc\",\n" +
                "  \"category\": \"electronics\"\n" +
                "}";

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/products/" + productId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Product Title"))
                .body("price", equalTo(99.99f));
    }
}
