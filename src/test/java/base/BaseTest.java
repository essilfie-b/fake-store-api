package base;

import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureJunit5.class)
public class BaseTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://fakestoreapi.com";
    }
}
