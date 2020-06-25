package com.semiz.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.models.PathItem.HttpMethod;

import com.semiz.db.entity.DbConfig;
import com.semiz.entity.ServiceItem;
import com.semiz.entity.ServiceItemParameter;
import com.semiz.entity.ServiceItemSqlType;

@ApplicationScoped
@Alternative
@Priority(1)
public class ServiceCatalogDbStore implements ServiceCatalogStore {

	Map<String, ServiceItem> services = new HashMap<>();
	Map<Long, DbConfig> dbConns = new HashMap<>();

	@ConfigProperty(name = "quarkus.datasource.url")
	String url;

	@ConfigProperty(name = "quarkus.datasource.driver")
	String driver;

	@ConfigProperty(name = "quarkus.datasource.username")
	String username;

	public Connection getConnection() throws Exception {
		Class.forName(driver);
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", username);
		conn = DriverManager.getConnection(url, connectionProps);
		return conn;
	}

	/**
	 * First initialization of services occurs before JPA is initialized Using plain
	 * JDBC though
	 */
	@Override
	public Collection<ServiceItem> loadServices() {
		try (Connection conn = getConnection();
				PreparedStatement stc = conn.prepareStatement("SELECT * FROM SERVCON");
				ResultSet rsc = stc.executeQuery();
				PreparedStatement st = conn.prepareStatement("SELECT * FROM SERV");
				ResultSet rs = st.executeQuery();
				PreparedStatement stp = conn.prepareStatement("SELECT * FROM SERVPAR");
				ResultSet rsp = stp.executeQuery();

		) {

			while (rsc.next()) {
				DbConfig conf = new DbConfig();
				conf.setId(rsc.getLong("id"));
				conf.setName(rsc.getString("name"));
				conf.setDbKind(rsc.getString("dbKind"));
				conf.setUrl(rsc.getString("url"));
				conf.setDriver(rsc.getString("driver"));
				conf.setUsername(rsc.getString("username"));
				conf.setPassword(rsc.getString("password"));
				conf.setMinSize(rsc.getInt("minSize"));
				conf.setMaxSize(rsc.getInt("maxSize"));
				dbConns.put(conf.getId(), conf);

			}

			while (rs.next()) {
				ServiceItem item = new ServiceItem();
				item.setId(rs.getLong("id"));
				item.setDescription(rs.getString("description"));
				item.setPath(rs.getString("path"));
				item.setHttpMethod(HttpMethod.valueOf(rs.getString("httpMethod")));
				item.setConsumes(rs.getString("consumes"));
				item.setProduces(rs.getString("produces"));
				item.setSqlType(ServiceItemSqlType.valueOf(rs.getString("sqlType")));
				item.setSql(rs.getString("sql"));
				item.setDbConfigId((Integer) rs.getObject("dbConfigId"));
				services.put(item.getOperationId(), item);
			}

			while (rsp.next()) {
				ServiceItemParameter param = new ServiceItemParameter();
				param.setId(rsp.getLong("id"));
				param.setName(rsp.getString("name"));
				param.setSchemaType(rsp.getString("schemaType"));
				param.setIn(rsp.getString("in"));
				param.setDescription(rsp.getString("description"));
				String operationId = rsp.getString("operationId");

				ServiceItem item = services.get(operationId);
				item.addParameter(param);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return services.values();
	}

	@Override
	@Transactional
	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		ServiceItem.persist(serviceItem);
		services.put(serviceItem.getOperationId(), serviceItem);
		return serviceItem;
	}

	@Override
	public ServiceItem getServiceItem(String serviceItemId) {
		return services.get(serviceItemId);
	}

	@Override
	public ServiceItem deleteServiceItem(String serviceItemId) {
		ServiceItem.findById(serviceItemId).delete();
		return services.remove(serviceItemId);
	}

	@Override
	public Collection<ServiceItem> getServiceItems() {
		return services.values();
	}

	@Override
	public DbConfig getConnection(Integer connectionId) {
		return dbConns.get(connectionId);
	}

	@Override
	public DbConfig deleteConnection(Integer connectionId) {
		DbConfig.findById(connectionId).delete();
		return dbConns.remove(connectionId);
	}

	@Override
	public DbConfig saveConnection(DbConfig connection) {
		DbConfig.persist(connection);
		dbConns.put(connection.getId(), connection);
		return connection;
	}

	@Override
	public Collection<DbConfig> getConnections() {
		return dbConns.values();
	}

}
