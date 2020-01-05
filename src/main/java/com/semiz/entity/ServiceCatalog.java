package com.semiz.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.semiz.db.boundary.DbConnection;
import com.semiz.db.entity.QueryResult;

@ApplicationScoped
public class ServiceCatalog {

	private static final Logger LOG = Logger.getLogger(ServiceCatalog.class);

	Map<Integer, ServiceItem> items = new HashMap<>();

	@Inject
	DbConnection conn;

	@ConfigProperty(name = "services.path")
	String servicesPath;

	public ServiceCatalog() {

	}

	public Map<Integer, ServiceItem> getItems() {
		return items;
	}

	public void setItems(Map<Integer, ServiceItem> items) {
		this.items = items;
	}

	public ServiceItem getItem(String id) {
		return this.items.get(id);
	}

	public QueryResult getSqlExecResult(ServiceItem item, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map bodyParameters) {
		return item.getSqlExecResult(conn, pathParameters, queryParameters, bodyParameters);
	}

	@PostConstruct
	public void loadServices() {
		try {
			List<File> resourceFiles = getResourceFiles(this.servicesPath);
			for (File resourceFile : resourceFiles) {
				ServiceItem item = toServiceItem(new FileInputStream(resourceFile));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<File> getResourceFiles(String path) {
		List<File> fileNames = new ArrayList<>();
		IOFileFilter dirFilter = new IOFileFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return dir.isFile();
			}

			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		};
		IOFileFilter fileFilter = new IOFileFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("json");
			}

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith("json");
			}
		};

		File f = null;
		try {
			for (Iterator<File> iter = FileUtils.iterateFilesAndDirs(new File(path), fileFilter, dirFilter); iter
					.hasNext();) {
				f = iter.next();
				if (f.isFile()) {
					fileNames.add(f);
				}
			}
		} catch (Exception e) {
			LOG.errorf("Error reading service description file: %s", f);
		}
		System.out.println(fileNames);
		return fileNames;
	}

	private ServiceItem toServiceItem(InputStream is) {
		JsonReader parser = Json.createReader(is);
		JsonObject object = parser.readObject();
		return null;
	}

}
