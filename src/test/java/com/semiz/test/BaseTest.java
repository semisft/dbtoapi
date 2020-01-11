package com.semiz.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;

import io.quarkus.test.TransactionalQuarkusTest;

@TransactionalQuarkusTest
public class BaseTest {

	@Inject
	DataSource ds;

	@Inject 
	TransactionManager tm;
	
	@AfterEach
	public void rollbackTransaction() {
		try {
			tm.setRollbackOnly();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	

	public void initDbForAll() {
		initDb("/initdb/init.sql");
	}
	
	public void initDb(String fileName) {
		try (Connection conn = ds.getConnection(); 
				Statement st = conn.createStatement();) {
			List<String> sqlLines = IOUtils.readLines(getClass().getResourceAsStream(fileName), StandardCharsets.UTF_8);
			for (String sql : sqlLines) {
				st.execute(sql);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String expectedString(String serviceName) {
		try {
			return IOUtils.resourceToString(serviceName+"/expected.result.json", 
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "NULL";
		}
	}
}
