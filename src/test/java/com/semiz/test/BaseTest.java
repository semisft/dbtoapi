package com.semiz.test;

import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.junit.jupiter.api.AfterEach;

import io.quarkus.test.TransactionalQuarkusTest;

@TransactionalQuarkusTest
public class BaseTest {

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
	
}
