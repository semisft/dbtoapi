package com.semiz.db.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.semiz.test.BaseTest;

import io.quarkus.test.TransactionalQuarkusTest;

@TransactionalQuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Serv6Test extends BaseTest {
	
	private static final String SERVICE_NAME = "/service6GET2PathParam";

	@Test
	public void testHelloEndpoint() throws IOException {
		given().
		when().
		get("/api"+SERVICE_NAME+"/{id}/{country}", 1, "USA").
		then().
		statusCode(200).and().
		body(is(super.expectedString(SERVICE_NAME)));
	}

	@BeforeAll
	public void initDb() {
		super.initDbForAll();
	}

}