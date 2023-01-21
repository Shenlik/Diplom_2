package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.request.CreateOrderRequest;
import org.example.request.UserFactory;
import org.example.respons.IngredientsData;
import org.example.respons.OrderResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.List;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
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

        var response = createUser(request);
        token = getToken(response.getBody().asString());
    }

    @AfterClass
    public static void cleanupUser() {
        deleteUser(token);
    }

    @Test
    @Description(value = "Тест проверяет создание заказа с авторизацией")
    public void shouldSuccessfullyCreateOrder() throws JsonProcessingException {
        var ingredients = getIngredients(token);
        var one = ingredients.getData().get(0);
        var two = ingredients.getData().get(1);

        var order = createOrder(token, List.of(one.get_id(), two.get_id()));

        assertTrue(order.isSuccess());
        assertEquals((long) one.getPrice() + two.getPrice(), (long) order.getOrder().getPrice());
    }

    @Test
    @Description(value = "Тест проверяет создание заказа с авторизацией без ингридентов")
    public void shouldBadRequestCreateOrder() throws JsonProcessingException {
        var request = new CreateOrderRequest(List.of());
        var requestAsJson = objectMapper.writeValueAsString(request);

        var response = given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post("/api/orders");

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
        var ingredients = getIngredients(token);
        var one = ingredients.getData().get(0);

        var order = createOrder(null, List.of(one.get_id()));

        assertTrue(order.isSuccess());
        assertNotNull(order.getOrder().getNumber());
    }

    @Test
    @Description(value = "Тест проверяет создание заказа с неверным хешем ингредиентов")
    public void shouldBadRequestWithIncorrectHash() throws JsonProcessingException {
        var incorrectId = "60d3b41abdacab0026a733c6";
        var request = new CreateOrderRequest(List.of(incorrectId));
        var requestAsJson = objectMapper.writeValueAsString(request);

        var response = given()
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post("/api/orders");

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("One or more ids provided are incorrect"))
                .and()
                .statusCode(HTTP_BAD_REQUEST);
    }


    private static OrderResponse createOrder(@Nullable String token, List<String> ingredients) throws JsonProcessingException {
        var request = new CreateOrderRequest(ingredients);
        var requestAsJson = objectMapper.writeValueAsString(request);

        var builder = given();
        if (token != null) {
            builder.header("Authorization", token);
        }
        var response = builder
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post("/api/orders");


        return objectMapper.readValue(response.getBody().asString(), OrderResponse.class);
    }

    private static IngredientsData getIngredients(@Nullable String token) throws JsonProcessingException {
        var builder = given();
        if (token != null) {
            builder.header("Authorization", token);
        }

        var response = builder.header("Content-type", "application/json")
                .and()
                .when()
                .get("/api/ingredients");


        return objectMapper.readValue(response.getBody().asString(), IngredientsData.class);
    }
}
