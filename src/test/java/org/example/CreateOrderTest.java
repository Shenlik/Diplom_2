package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.OrderClient;
import org.example.api.UserClient;
import org.example.request.CreateOrderRequest;
import org.example.request.UserFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static org.example.api.URLs.ORDERS_URL;
import static org.example.config.ObjectMapperConfig.OBJECT_MAPPER;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CreateOrderTest extends AbstractTest {

    private static String email;
    private static String token;

    @BeforeClass
    public static void shouldSuccessfullyCreateUserForChange() throws JsonProcessingException {
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
    @Description(value = "Тест проверяет создание заказа с авторизацией")
    public void shouldSuccessfullyCreateOrder() throws JsonProcessingException {
        var ingredients = OrderClient.getIngredients(token);
        var one = ingredients.getData().get(0);
        var two = ingredients.getData().get(1);

        var order = OrderClient.createOrder(token, List.of(one.get_id(), two.get_id()));

        assertTrue(order.isSuccess());
        assertEquals((long) one.getPrice() + two.getPrice(), (long) order.getOrder().getPrice());
    }

    @Test
    @Description(value = "Тест проверяет создание заказа с авторизацией без ингридентов")
    public void shouldBadRequestCreateOrder() throws JsonProcessingException {
        var request = new CreateOrderRequest(List.of());
        var requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

        var response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post(ORDERS_URL);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    @Description(value = "Тест проверяет создание заказа неавторизованным пользователем")
    public void shouldSuccessfullyCreateOrderWithoutLogin() throws JsonProcessingException {
        var ingredients = OrderClient.getIngredients(token);
        var one = ingredients.getData().get(0);

        var order = OrderClient.createOrder(null, List.of(one.get_id()));

        assertTrue(order.isSuccess());
        assertNotNull(order.getOrder().getNumber());
    }

    @Test
    @Description(value = "Тест проверяет создание заказа с неверным хешем ингредиентов")
    public void shouldBadRequestWithIncorrectHash() throws JsonProcessingException {
        var incorrectId = "60d3b41abdacab0026a733c6";
        var request = new CreateOrderRequest(List.of(incorrectId));
        var requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

        var response = given()
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post(ORDERS_URL);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("One or more ids provided are incorrect"))
                .and()
                .statusCode(HTTP_BAD_REQUEST);
    }


}
