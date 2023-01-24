package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.api.UserClient;
import org.example.request.UserFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ChangeDataTest extends AbstractTest {

    private static final File changeName = new File("src/test/resources/changeUser/ChangeName.json");
    private static final File changePassword = new File("src/test/resources/changeUser/ChangePassword.json");
    private static final File changeEmail = new File("src/test/resources/changeUser/ChangeEmail.json");
    private static String email;
    private static String token;

    @Before
    public void shouldSuccessfullyCreateUserForChange() throws JsonProcessingException {
        setUp();
        email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);

        var response = UserClient.createUser(request);
        token = UserClient.getToken(response.getBody().asString());
    }

    @After
    public void cleanupUser() {
        UserClient.deleteUser(token);
    }

    @Test
    @Description(value = "Тест проверяет изменение email пользователя с авторизацией")
    public void shouldSuccessfullyChangeEmail() {
        Response response = UserClient.changeData(changeEmail, token);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("user.email", equalTo("1234567@yandex.ru"))
                .body("user.name", equalTo("Username"))
                .and()
                .statusCode(HTTP_OK);

    }

    @Test
    @Description(value = "Тест проверяет изменение password пользователя с авторизацией")
    public void shouldSuccessfullyChangePassword() throws JsonProcessingException {
        Response response = UserClient.changeData(changePassword, token);

        response.then()
                .assertThat()
                .body("success", is(true))
                .and()
                .statusCode(HTTP_OK);

        UserClient.loginUser(email, "password22")
                .then()
                .statusCode(HTTP_OK);
    }


    @Test
    @Description(value = "Тест проверяет изменение name пользователя с авторизацией")
    public void shouldSuccessfullyChangeName() {
        Response response = UserClient.changeData(changeName, token);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("user.email", equalTo(email.toLowerCase(Locale.ROOT)))
                .body("user.name", equalTo("Username11"))
                .and()
                .statusCode(HTTP_OK);

    }

    @Test
    @Description(value = "Тест проверяет изменение email пользователя без авторизации")
    public void shouldUnauthorizedOnChangeEmail() {
        Response response = UserClient.changeData(changeEmail, null);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @Description(value = "Тест проверяет изменение password пользователя без авторизации")
    public void shouldUnauthorizedOnChangePassword() {
        Response response = UserClient.changeData(changePassword, null);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @Description(value = "Тест проверяет изменение name пользователя без авторизации")
    public void shouldUnauthorizedOnChangeName() {
        Response response = UserClient.changeData(changeName, null);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);
    }
}
