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
public class Serv4Test extends BaseTest {
	
	private static final String SERVICE_NAME = "/service4POSTBodyParam";

	private static String payload = "{" +
	        "  \"id\": 2, \"name\":\"Sour-cherry\""+
	        "}";
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
	}

	@BeforeAll
	public void initDb() {
		super.initDbForAll();
	}

}