package org.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.request.CreateOrderRequest;
import org.example.respons.IngredientsData;
import org.example.respons.OrderResponse;

import javax.annotation.Nullable;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.example.api.URLs.INGREDIENTS_URL;
import static org.example.api.URLs.ORDERS_URL;
import static org.example.config.ObjectMapperConfig.OBJECT_MAPPER;

public class OrderClient {


    public static OrderResponse createOrder(@Nullable String token, List<String> ingredients) throws JsonProcessingException {
        var request = new CreateOrderRequest(ingredients);
        var requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

        var builder = given();
        if (token != null) {
            builder.header("Authorization", token);
        }
        var response = builder
                .header("Content-type", "application/json")
                .body(requestAsJson)
                .and()
                .when()
                .post(ORDERS_URL);


        return OBJECT_MAPPER.readValue(response.getBody().asString(), OrderResponse.class);
    }

    public static IngredientsData getIngredients(@Nullable String token) throws JsonProcessingException {
        var builder = given();
        if (token != null) {
            builder.header("Authorization", token);
        }

        var response = builder.header("Content-type", "application/json")
                .and()
                .when()
                .get(INGREDIENTS_URL);


        return OBJECT_MAPPER.readValue(response.getBody().asString(), IngredientsData.class);
    }
}
