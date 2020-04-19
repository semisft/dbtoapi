package com.semiz.db.entity;

public class DbKind {
	private String dbKind;
	private String driverName;
	private String jdbcUrlTemplate;
	
	public DbKind(String dbKind, String driverName, String jdbcUrlTemplate) {
		super();
		this.dbKind = dbKind;
		this.driverName = driverName;
		this.jdbcUrlTemplate = jdbcUrlTemplate;
	}

	public String getDbKind() {
		return dbKind;
	}

	public void setDbKind(String dbKind) {
		this.dbKind = dbKind;
	}

	public String getJdbcUrlTemplate() {
		return jdbcUrlTemplate;
	}

	public void setJdbcUrlTemplate(String jdbcUrlTemplate) {
		this.jdbcUrlTemplate = jdbcUrlTemplate;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	
	
}
