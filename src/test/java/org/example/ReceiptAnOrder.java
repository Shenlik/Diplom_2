package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.UserClient;
import org.example.request.UserFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.example.api.URLs.ORDERS_URL;
import static org.hamcrest.Matchers.*;

public class ReceiptAnOrder extends AbstractTest{

    private static String email;
    private static String token;

    @BeforeClass
    public static void shouldSuccessfullyCreateUserForReceipt() throws JsonProcessingException {
        setUp();
        email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);

        var response = UserClient.createUser(request);
        token = UserClient.getToken(response.getBody().asString());
    }

    @AfterClass
    public static void cleanupUser() {
        UserClient.deleteUser(token);
    }

    @Test
    @Description(value = "Тест проверяет получения заказа авторизованного пользователя")
    public void shouldSuccessfullyReceiptOrder() {
        Response response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(ORDERS_URL);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("orders", hasSize(0))
                .body("total", notNullValue())
                .body("totalToday", notNullValue())
                .and()
                .statusCode(HTTP_OK);
    }


    @Test
    @Description(value = "Тест проверяет получения заказа неавторизованного пользователя")
    public void shouldUnauthorizedWithoutLogin()   {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(ORDERS_URL);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);

    }
}
