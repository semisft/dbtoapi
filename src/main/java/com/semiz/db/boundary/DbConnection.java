package com.semiz.db.boundary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import com.semiz.db.entity.NamedParameterPreparedStatement;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;
import com.semiz.entity.DbConfig;

import io.quarkus.agroal.runtime.AbstractDataSourceProducer;
import io.quarkus.agroal.runtime.DataSourceBuildTimeConfig;
import io.quarkus.agroal.runtime.DataSourceRuntimeConfig;

@ApplicationScoped
public class DbConnection {

	@Inject
	AbstractDataSourceProducer dbProducer;

	Map<String, DataSource> dataSources = new HashMap<>();

	private DataSource getDatasource(DbConfig dbConfig) {

		String dataSourceName = "java:/ds" + dbConfig.getId();
		DataSource ds = null;
		if (dataSources.containsKey(dataSourceName)) {
			ds = dataSources.get(dataSourceName);
		} else {
			DataSourceBuildTimeConfig dataSourceBuildTimeConfig = new DataSourceBuildTimeConfig();
			dataSourceBuildTimeConfig.driver = Optional.of(dbConfig.getDriver());

			DataSourceRuntimeConfig dataSourceRuntimeConfig = new DataSourceRuntimeConfig();
			dataSourceRuntimeConfig.url = Optional.of(dbConfig.getUrl());
			dataSourceRuntimeConfig.username = Optional.of(dbConfig.getUsername());
			dataSourceRuntimeConfig.password = Optional.of(dbConfig.getPassword());
			dataSourceRuntimeConfig.minSize = dbConfig.getMinSize();
			dataSourceRuntimeConfig.maxSize = dbConfig.getMaxSize();
			dataSourceRuntimeConfig.transactionIsolationLevel = Optional.ofNullable(null);
			dataSourceRuntimeConfig.newConnectionSql = Optional.ofNullable(null);
			dataSourceRuntimeConfig.enableMetrics = true;
			dataSourceRuntimeConfig.credentialsProvider = Optional.ofNullable(null);
			dataSourceRuntimeConfig.initialSize = Optional.ofNullable(null);
			dataSourceRuntimeConfig.acquisitionTimeout = Optional.ofNullable(null);
			dataSourceRuntimeConfig.backgroundValidationInterval = Optional.ofNullable(null);
			dataSourceRuntimeConfig.validationQuerySql = Optional.ofNullable(null);
			dataSourceRuntimeConfig.idleRemovalInterval = Optional.ofNullable(null);
			dataSourceRuntimeConfig.leakDetectionInterval = Optional.ofNullable(null);
			dataSourceRuntimeConfig.maxLifetime = Optional.ofNullable(null);

			ds = dbProducer.createDataSource(dataSourceName, dataSourceBuildTimeConfig,
					Optional.of(dataSourceRuntimeConfig));
			dataSources.put(dataSourceName, ds);
		}
		return ds;
	}

	public QueryResult execute(DbConfig dbConfig, String sql, Map<String, Object> parameters, boolean isSelect) {
		QueryResult result = new QueryResult();

		Entry<String, Object> lastEntry = null;
		try (final Connection conn = getDatasource(dbConfig).getConnection();
				final NamedParameterPreparedStatement st = NamedParameterPreparedStatement
						.createNamedParameterPreparedStatement(conn, sql)) {

			for (Entry<String, Object> entry : parameters.entrySet()) {
				if (st.hasNamedParameter(entry.getKey())) {
					lastEntry = entry;
					st.setObject(entry.getKey(), entry.getValue());
				}
			}

			if (isSelect) {
				ResultSet resultList = st.executeQuery();
				ResultSetMetaData rsmd = resultList.getMetaData();

				List<String> columnNames = new ArrayList<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String name = rsmd.getColumnLabel(i);
					columnNames.add(name);
				}
				while (resultList.next()) {
					LinkedHashMap<String, Object> row = new LinkedHashMap<>();
					for (String columnName : columnNames) {
						row.put(columnName, resultList.getObject(columnName));
					}
					result.getResultList().add(row);

				}
				result.setColumns(columnNames);
				result.setRecordCount(result.getResultList().size());
				result.setResultCode(Response.Status.OK.getStatusCode());
			} else {
				int recordCount = st.executeUpdate();
				result.setRecordCount(recordCount);
				result.setResultCode(Response.Status.OK.getStatusCode());
			}
		} catch (Exception e) {
			throw new ParameterException(lastEntry != null ? lastEntry.getKey() : "",
					lastEntry != null ? lastEntry.getValue() : "", e.getMessage());
		}

		return result;
	}

	public QueryResult select(DbConfig dbConfig, String sql, Map<String, Object> parameters) {
		return execute(dbConfig, sql, parameters, true);
	}

	public QueryResult insert(DbConfig dbConfig, String sql, Map<String, Object> parameters) {
		return execute(dbConfig, sql, parameters, false);
	}

	public QueryResult update(DbConfig dbConfig, String sql, Map<String, Object> parameters) {
		return execute(dbConfig, sql, parameters, false);
	}

	public QueryResult delete(DbConfig dbConfig, String sql, Map<String, Object> parameters) {
		return execute(dbConfig, sql, parameters, false);
	}

}
