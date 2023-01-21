package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
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

    private static String email;
    private static String token;

    @Before
    public  void shouldSuccessfullyCreateUserForChange() throws JsonProcessingException {
        setUp();
        email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);

        var response = createUser(request);
        token = getToken(response.getBody().asString());
    }

    @After
    public  void cleanupUser() {
        deleteUser(token);
    }

    @Test
    @Description(value = "Тест проверяет изменение email пользователя с авторизацией")
    public void shouldSuccessfullyChangeEmail() {

        File json = new File("src/test/resources/changeUser/ChangeEmail.json");

        Response response = changeData(json, token);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("user.email", equalTo("1234567@yandex.ru"))
                .body("user.name", equalTo("Username"))
                .and()
                .statusCode(HTTP_OK);

    }
// proverit username
    @Test
    @Description(value = "Тест проверяет изменение password пользователя с авторизацией")
    public void shouldSuccessfullyChangePassword() throws JsonProcessingException {
        File json = new File("src/test/resources/changeUser/changePassword.json");

        Response response = changeData(json, token);

        response.then()
                .assertThat()
                .body("success", is(true))
                .and()
                .statusCode(HTTP_OK);

        loginUser(email, "password22")
                .then()
                .statusCode(HTTP_OK);
    }


    @Test
    @Description(value = "Тест проверяет изменение name пользователя с авторизацией")
    public void shouldSuccessfullyChangeName() {
        File json = new File("src/test/resources/changeUser/changeName.json");

        Response response = changeData(json, token);

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

        File json = new File("src/test/resources/changeUser/ChangeEmail.json");

        Response response = changeData(json, null);

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
        File json = new File("src/test/resources/changeUser/ChangePassword.json");

        Response response = changeData(json, null);

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
        File json = new File("src/test/resources/changeUser/ChangeName.json");

        Response response = changeData(json, null);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);
    }
}
