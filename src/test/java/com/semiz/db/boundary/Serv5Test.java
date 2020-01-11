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
public class Serv5Test extends BaseTest {
	
	private static final String SERVICE_NAME = "/service5POSTFormParam";

	@Test
	public void testHelloEndpoint() throws IOException {
		
		
		given().
		when().
		contentType(ContentType.TEXT).
		urlEncodingEnabled(true).
        formParam("id", 2).
        formParam("name", "Apricot").
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