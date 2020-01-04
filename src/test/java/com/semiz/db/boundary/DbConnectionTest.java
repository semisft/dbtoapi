package com.semiz.db.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbConnectionTest {
	
	DbConnection cut = new DbConnection();
	
	public void testRegexReplace() {
		String strTest = "SELECT * FROM TBL WHERE name=:name AND age>= :age";
		String actual = cut.replaceNamedParameters(strTest);
		String expected = "SELECT * FROM TBL WHERE name=? AND age>= ?";
		assertEquals(expected, actual, "Replacement error");
		
		
	}

}
