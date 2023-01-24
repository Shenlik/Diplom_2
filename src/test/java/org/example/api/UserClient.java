package org.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.example.request.CreateUserRequest;
import org.example.request.LoginUserRequest;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static org.example.api.URLs.*;
import static org.example.config.ObjectMapperConfig.OBJECT_MAPPER;

public class UserClient {

    public static Response createUser(CreateUserRequest createUserRequest) throws JsonProcessingException {
        var requestAsJson = OBJECT_MAPPER.writeValueAsString(createUserRequest);

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(requestAsJson)
                .when()
                .post(REGISTER_USER_URL);
    }

    public static void deleteUser(String token) {
        given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete(USER_URL)
                .then()
                .statusCode(HTTP_ACCEPTED);
    }

    public static Response loginUser(String email, String password) throws JsonProcessingException {
        var request = new LoginUserRequest(email, password);
        var requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

        return given()
                .header("Content-type", "application/json")
                .and()
                .body(requestAsJson)
                .when()
                .post(LOGIN_USER_URL);
    }

    public static Response loginUser(File json) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post(LOGIN_USER_URL);
    }

    public static Response changeData(File json, @Nullable String token) {
        var query = given();
        if (token != null) {
            query.header("Authorization", token);
        }
        return query.header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .patch(USER_URL);
    }

    @Nullable
    public static String getToken(String createUserResponse) throws JsonProcessingException {
        var userResponse = OBJECT_MAPPER.readValue(createUserResponse, HashMap.class);
        return (String) userResponse.get("accessToken");
    }
}
