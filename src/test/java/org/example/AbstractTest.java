package org.example;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

import static org.example.api.URLs.STELLARBURGERS_URL;

public class AbstractTest {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = STELLARBURGERS_URL;
    }

}



