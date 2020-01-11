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
public class Serv2Test extends BaseTest {
	
	private static final String SERVICE_NAME = "/service2GETPathParam";

	@Test
	public void testHelloEndpoint() throws IOException {
		given().
		when().get("/api"+SERVICE_NAME+"/2").
		then().
		statusCode(200).and().
		body(is(super.expectedString(SERVICE_NAME)));
	}

	@BeforeAll
	public void initDb() {
		super.initDbForAll();
	}

}