package com.semiz.db.boundary;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import com.semiz.db.entity.NamedParameterPreparedStatement;
import com.semiz.db.entity.ParameterException;
import com.semiz.db.entity.QueryResult;

@ApplicationScoped
public class DbConnection {
	@Inject
	DataSource ds;

	public QueryResult select(String sql, Map<String, Object> parameters) {
		QueryResult result = new QueryResult();

		Entry<String, Object> lastEntry = null;
		try (final NamedParameterPreparedStatement st = NamedParameterPreparedStatement
				.createNamedParameterPreparedStatement(ds.getConnection(), sql)) {

			for (Entry<String, Object> entry : parameters.entrySet()) {
				if (st.hasNamedParameter(entry.getKey())) {
					lastEntry = entry;
					st.setObject(entry.getKey(), entry.getValue());
				}
			}

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
			result.setResultCode(Response.Status.OK.getStatusCode());
		} catch (Exception e) {
			throw new ParameterException(lastEntry != null ? lastEntry.getKey() : "",
					lastEntry != null ? lastEntry.getValue() : "", e.getMessage());
		}

		return result;
	}

}
