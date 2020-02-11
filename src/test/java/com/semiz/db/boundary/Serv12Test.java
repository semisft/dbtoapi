package com.semiz.db.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.semiz.test.BaseTest;

import io.quarkus.test.TransactionalQuarkusTest;
import io.restassured.http.ContentType;

@TransactionalQuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Serv12Test extends BaseTest {
	
	private static final String SERVICE_NAME = "/service12POSTArrayBodyParam";

	private static String payload = "[" +
	        "{\"id\": 5, \"name\":\"Sour-cherry\"},"+
	        "{\"id\": 6, \"name\":\"Peach\"}"+
	        "]";
	@Test
	public void testHelloEndpoint() throws IOException {
		
		
		given().
		when().
		contentType(ContentType.JSON).
        body(payload).
        post("/api"+SERVICE_NAME).
		then().
		statusCode(200).and().
		body(is(super.expectedString(SERVICE_NAME)));

		given().
		when().
		contentType(ContentType.JSON).
		queryParam("id", 5).
		queryParam("id", 6).
        get("/api/fruit_count").
		then().
		statusCode(200).and().
		body(is(super.expectedString(SERVICE_NAME, "fruit_count.expected.result.json")));
	}

	@BeforeAll
	public void initDb() {
		super.initDbForAll();
	}

}