package com.semiz.db.entity;

import java.io.InputStream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.validation.constraints.NotNull;

public class DbConfig {

	Integer id;

	@NotNull
	String name;

	String dbKind;

	@NotNull
	String url;

	@NotNull
	String driver;

	@NotNull
	String username;

	@NotNull
	String password;

	Integer minSize;
	Integer maxSize;

	public DbConfig() {

	}

	public DbConfig(Integer id) {
		this();
		this.setId(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMinSize() {
		return minSize;
	}

	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public String getDbKind() {
		return dbKind;
	}

	public void setDbKind(String dbKind) {
		this.dbKind = dbKind;
	}

	public static DbConfig toDbConfig(InputStream is) {
		Jsonb jsonb = JsonbBuilder.create();
		DbConfig result = jsonb.fromJson(is, DbConfig.class);
		return result;
	}

}
