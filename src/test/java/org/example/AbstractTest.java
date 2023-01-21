package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.request.CreateUserRequest;
import org.example.request.LoginUserRequest;
import org.junit.BeforeClass;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_ACCEPTED;

public class AbstractTest {

    protected static ObjectMapper objectMapper;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";

        objectMapper = new ObjectMapper();
        objectMapper.disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected static Response createUser(CreateUserRequest createUserRequest) throws JsonProcessingException {
        var requestAsJson = objectMapper.writeValueAsString(createUserRequest);

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(requestAsJson)
                .when()
                .post("/api/auth/register");
    }

    protected static void deleteUser(String token) {
        given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(HTTP_ACCEPTED);
    }

    protected static Response loginUser(String email, String password) throws JsonProcessingException {
        var request = new LoginUserRequest(email, password);
        var requestAsJson = objectMapper.writeValueAsString(request);

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(requestAsJson)
                .when()
                .post("api/auth/login");
    }

    protected static Response loginUser(File json) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("api/auth/login");
    }

    protected static Response changeData(File json, @Nullable String token) {
        var query = given();
        if (token != null) {
            query.header("Authorization", token);
        }
        return query.header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch("api/auth/user");
    }

    protected static String getToken(String createUserResponse) throws JsonProcessingException {
        var userResponse = objectMapper.readValue(createUserResponse, HashMap.class);
        return (String) userResponse.get("accessToken");
    }


}



