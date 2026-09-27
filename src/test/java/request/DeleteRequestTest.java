package request;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("FakeStore API Testing")
@Feature("DELETE Requests")
@Severity(SeverityLevel.NORMAL)
@Story("Delete Product")

public class DeleteRequestTest extends BaseTest {

    @Test
    @DisplayName("Delete a product by ID and validate the response")
    public void deleteProduct() {
        int productId = 1;

        RestAssured
                .given()
                .when()
                .delete("/products/" + productId)
                .then()
                .statusCode(200);
    }
}
