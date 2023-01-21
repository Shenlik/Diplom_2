package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.request.UserFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class LoginUserTest extends AbstractTest {

    private static String email;
    private static String password;
    private static String token;

    @BeforeClass
    public static void shouldSuccessfullyCreateUserForLogin() throws JsonProcessingException {
        setUp();
        email = RandomStringUtils.randomAlphabetic(6) + "@mailto.plus";
        var request = UserFactory.createUserRequest(email);
        password = request.getPassword();

        var response = createUser(request);
        token = getToken(response.getBody().asString());
    }

    @AfterClass
    public static void cleanupUser() {
        deleteUser(token);
    }

    @Test
    @Description(value = "Тест проверяет логин пользователя")
    public void shouldSuccessfullyLogin() throws JsonProcessingException {

        Response response = loginUser(email, password);

        response.then()
                .assertThat()
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email.toLowerCase(Locale.ROOT)))
                .body("user.name", equalTo("Username"))
                .and()
                .statusCode(HTTP_OK);

    }

    @Test
    @Description(value = "Тест проверяет неправильно указанный email пользователя")
    public void shouldNotFoundWrongEmail() {

        File json = new File("src/test/resources/loginUser/loginUserWrongEmail.json");

        Response response = loginUser(json);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    @Description(value = "Тест проверяет неправильно указанный  password пользователя")
    public void shouldNotFoundWrongPassword() {

        File json = new File("src/test/resources/loginUser/loginUserWrongPassword.json");

        Response response = loginUser(json);

        response.then()
                .assertThat()
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(HTTP_UNAUTHORIZED);

    }
}
