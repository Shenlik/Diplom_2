package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.UserClient;
import org.example.request.UserFactory;
import org.junit.Test;

import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.*;

public class CreateUserTest extends AbstractTest {


    @Test
    @Description(value = "Тест проверяет успешное создание пользователя")
    public void shouldSuccessfullyCreateUser() throws JsonProcessingException {
        var email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);

        var response = UserClient.createUser(request);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("user.email",equalTo(email.toLowerCase(Locale.ROOT)))
                .body("user.name", equalTo(request.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .and()
                .statusCode(HTTP_OK);

        var bearer = UserClient.getToken(response.getBody().asString());
        UserClient.deleteUser(bearer);
    }



    @Test
    @Description(value = "Тест проверяет создание пользователя, что уже есть в системе")
    public void should403WhenRegisterWithExistingMail() throws JsonProcessingException {
        var email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);

        UserClient.createUser(request);
        var response = UserClient.createUser(request);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message",equalTo("User already exists"))
                .and()
                .statusCode(HTTP_FORBIDDEN);

        var bearer = UserClient.getToken(response.getBody().asString());
        if(bearer != null) {
            UserClient.deleteUser(bearer);
        }
    }

    @Test
    @Description(value = "Тест проверяет создание пользователя без обязательного поля email")
    public void should403WhenCreateWithoutEmail() throws JsonProcessingException {
        var request = UserFactory.createWithoutEmailRequest();

        Response response = UserClient.createUser(request);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(HTTP_FORBIDDEN);

        var bearer = UserClient.getToken(response.getBody().asString());
        if (bearer != null) {
            UserClient.deleteUser(bearer);
        }
    }

    @Test
    @Description(value = "Тест проверяет создание пользователя без обязательного поля password")
    public void should403WhenCreateWithoutPassword() throws JsonProcessingException {
        var request = UserFactory.createWithoutPasswordRequest();

        Response response = UserClient.createUser(request);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(HTTP_FORBIDDEN);

        var bearer = UserClient.getToken(response.getBody().asString());
        if (bearer != null) {
            UserClient.deleteUser(bearer);
        }
    }

    @Test
    @Description(value = "Тест проверяет создание пользователя без обязательного поля name")
    public void should403WhenCreateWithoutName() throws JsonProcessingException {
        var request = UserFactory.createWithoutNameRequest();

        Response response = UserClient.createUser(request);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message",equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(HTTP_FORBIDDEN);

        var bearer = UserClient.getToken(response.getBody().asString());
        if(bearer != null) {
            UserClient.deleteUser(bearer);
        }
    }
}
