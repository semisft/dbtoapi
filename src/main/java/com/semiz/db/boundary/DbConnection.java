package com.semiz.db.boundary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import com.semiz.db.entity.DbConfig;
import com.semiz.db.entity.NamedParameterPreparedStatement;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;

import io.quarkus.agroal.runtime.AbstractDataSourceProducer;
import io.quarkus.agroal.runtime.DataSourceJdbcBuildTimeConfig;
import io.quarkus.agroal.runtime.DataSourceJdbcRuntimeConfig;
import io.quarkus.datasource.runtime.DataSourceBuildTimeConfig;
import io.quarkus.datasource.runtime.DataSourceRuntimeConfig;

@ApplicationScoped
public class DbConnection {

	public static final Integer USE_DEFAULT_DS = -999;

	private static final String PARAM_PREFIX = ":";

	@Inject
	DataSource defaultDs;

	@Inject
	AbstractDataSourceProducer dbProducer;

	Map<String, DataSource> dataSources = new HashMap<>();

	private DataSource getDatasource(DbConfig dbConfig) {
		DataSource ds = null;
		if (dbConfig == null || USE_DEFAULT_DS.equals(dbConfig.getId())) {
			ds = defaultDs;
		} 
		else {
			String dataSourceName = "ds-" + dbConfig.getId();
			ds = dataSources.get(dataSourceName);
			if (ds == null) {
				ds = newDataSource(dbConfig, dataSourceName);
				dataSources.put(dataSourceName, ds);
			}
		}
		return ds;
	}

	private DataSource newDataSource(DbConfig dbConfig, String dataSourceName) {
		DataSourceBuildTimeConfig dataSourceBuildTimeConfig = new DataSourceBuildTimeConfig();
		dataSourceBuildTimeConfig.dbKind = Optional.ofNullable(dbConfig.getDbKind());

		DataSourceJdbcBuildTimeConfig dataSourceJdbcBuildTimeConfig = new DataSourceJdbcBuildTimeConfig();
		dataSourceJdbcBuildTimeConfig.driver = Optional.of(dbConfig.getDriver());
		dataSourceJdbcBuildTimeConfig.enableMetrics = Optional.of(Boolean.TRUE);

		DataSourceRuntimeConfig dataSourceRuntimeConfig = new DataSourceRuntimeConfig();
		dataSourceRuntimeConfig.username = Optional.of(dbConfig.getUsername());
		dataSourceRuntimeConfig.password = Optional.of(dbConfig.getPassword());
		dataSourceRuntimeConfig.credentialsProvider = Optional.ofNullable(null);

		DataSourceJdbcRuntimeConfig dataSourceJdbcRuntimeConfig = new DataSourceJdbcRuntimeConfig();
		dataSourceJdbcRuntimeConfig.url = Optional.of(dbConfig.getUrl());
		dataSourceJdbcRuntimeConfig.minSize = dbConfig.getMinSize();
		dataSourceJdbcRuntimeConfig.initialSize = OptionalInt.of(dbConfig.getMinSize());
		dataSourceJdbcRuntimeConfig.maxSize = dbConfig.getMaxSize();
		dataSourceJdbcRuntimeConfig.transactionIsolationLevel = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.newConnectionSql = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.acquisitionTimeout = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.backgroundValidationInterval = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.validationQuerySql = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.idleRemovalInterval = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.leakDetectionInterval = Optional.ofNullable(null);
		dataSourceJdbcRuntimeConfig.maxLifetime = Optional.ofNullable(null);

		DataSource ds = dbProducer.createDataSource(dataSourceName, dataSourceBuildTimeConfig, 
				dataSourceJdbcBuildTimeConfig, dataSourceRuntimeConfig, dataSourceJdbcRuntimeConfig, 
				null,null,null,
				dbConfig.getDbKind(), dbConfig.getDriver(), true, false);
		return ds;
	}

	@Transactional
	public QueryResult execute(DbConfig dbConfig, String sql, Map<String, Object> parameters, boolean isSelect,
			List<Map<String, Object>> bodyParameters) {
		QueryResult result = new QueryResult();

		boolean multiUpdateBatchStmt = !isSelect && bodyParameters.size() > 1;

		Map<String, Object> parametersProcessed = new LinkedHashMap<>();
		// change SQL for multi-valued parameters
		for (Entry<String, Object> entry : parameters.entrySet()) {
			if (ArrayList.class.equals(entry.getValue().getClass())) {
				List<String> newParameterNames = new ArrayList<>();
				List list = (ArrayList) entry.getValue();
				for (int i = 0; i < list.size(); i++) {
					Object val = list.get(i);
					String newParameterKey = entry.getKey() + i;
					parametersProcessed.put(newParameterKey, val);
					newParameterNames.add(PARAM_PREFIX + newParameterKey);
				}
				String valuesStr = newParameterNames.stream().collect(Collectors.joining(","));
				sql = sql.replace(PARAM_PREFIX + entry.getKey(), valuesStr);
			} else {
				parametersProcessed.put(entry.getKey(), entry.getValue());
			}
		}

		Entry<String, Object> lastEntry = null;
		try (final Connection conn = getDatasource(dbConfig).getConnection();
				final NamedParameterPreparedStatement st = NamedParameterPreparedStatement
						.createNamedParameterPreparedStatement(conn, sql)) {

			for (Entry<String, Object> entry : parametersProcessed.entrySet()) {
				if (st.hasNamedParameter(entry.getKey())) {
					lastEntry = entry;
					st.setObject(entry.getKey(), entry.getValue());
				}
			}
			for (Iterator iterator = bodyParameters.iterator(); iterator.hasNext();) {
				Map<String, Object> row = (Map<String, Object>) iterator.next();
				for (Entry<String, Object> entry : row.entrySet()) {
					if (st.hasNamedParameter(entry.getKey())) {
						lastEntry = entry;
						st.setObject(entry.getKey(), entry.getValue());
					}
				}
				if (multiUpdateBatchStmt) {
					st.addBatch();
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
				int recordCount = 0;
				if (multiUpdateBatchStmt) {
					int[] recordCountInts = st.executeBatch();
					for (int count : recordCountInts) {
						recordCount += count;
					}
				} else {
					recordCount = st.executeUpdate();
				}
				result.setRecordCount(recordCount);
				result.setResultCode(Response.Status.OK.getStatusCode());
			}
		} catch (Exception e) {
			throw new ParameterException(lastEntry != null ? lastEntry.getKey() : "",
					lastEntry != null ? lastEntry.getValue() : "", e.getMessage());
		}

		return result;
	}

	public QueryResult select(DbConfig dbConfig, String sql, Map<String, Object> parameters,
			List<Map<String, Object>> bodyParameters) {
		return execute(dbConfig, sql, parameters, true, bodyParameters);
	}

	public QueryResult insert(DbConfig dbConfig, String sql, Map<String, Object> parameters,
			List<Map<String, Object>> bodyParameters) {
		return execute(dbConfig, sql, parameters, false, bodyParameters);
	}

	public QueryResult update(DbConfig dbConfig, String sql, Map<String, Object> parameters,
			List<Map<String, Object>> bodyParameters) {
		return execute(dbConfig, sql, parameters, false, bodyParameters);
	}

	public QueryResult delete(DbConfig dbConfig, String sql, Map<String, Object> parameters,
			List<Map<String, Object>> bodyParameters) {
		return execute(dbConfig, sql, parameters, false, bodyParameters);
	}

}
