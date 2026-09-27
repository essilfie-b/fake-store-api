package request;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Epic("FakeStore API Testing")
@Feature("GET Requests")
public class GetRequestTest extends BaseTest {

    @Test
    @DisplayName("Get all products and validate the response")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get All Products")
    @Description("Verify that all products are fetched successfully from /products endpoint.")
    public void getAllProducts() {
        RestAssured
                .given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].title", notNullValue());
    }

    @Test
    @DisplayName("Get a single product by ID and validate the response")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get Single Product")
    @Description("Verify that a specific product can be fetched successfully using /products/{id}.")
    public void getSingleProduct() {
        int productId = 1;

        RestAssured
                .given()
                .when()
                .get("/products/" + productId)
                .then()
                .statusCode(200)
                .body("id", equalTo(productId));
    }
}
