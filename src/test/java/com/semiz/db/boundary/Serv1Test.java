package com.semiz.db.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.semiz.test.BaseTest;

import io.quarkus.test.TransactionalQuarkusTest;

@TransactionalQuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Serv1Test extends BaseTest {

	@Inject
	DataSource ds;

	@Test
	public void testHelloEndpoint() throws IOException {
		given().when().get("/api/serv1").
		then().
		statusCode(200).and().
		body(is(
				IOUtils.resourceToString("/service1/getNoParams.result.json", 
						StandardCharsets.UTF_8)));
	}

	@BeforeAll
	public void initDb() {
		try (Connection conn = ds.getConnection(); 
				Statement st = conn.createStatement();) {
			List<String> sqlLines = IOUtils.readLines(getClass().getResourceAsStream("/service1/getNoParams.sql"), StandardCharsets.UTF_8);
			for (String sql : sqlLines) {
				st.execute(sql);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}