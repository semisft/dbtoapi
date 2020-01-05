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
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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

	public ServiceItem getItem(Integer id) {
		return this.items.get(id);
	}

	public QueryResult getSqlExecResult(ServiceItem item, MultivaluedMap<String, String> pathParameters,
			MultivaluedMap<String, String> queryParameters, Map bodyParameters) {
		return item.getSqlExecResult(conn, pathParameters, queryParameters, bodyParameters);
	}

	@PostConstruct
	public void loadServices() {
		File resourceFile = null;
		try {
			List<File> resourceFiles = getResourceFiles(this.servicesPath);
			for (Iterator iterator = resourceFiles.iterator(); iterator.hasNext();) {
				resourceFile = (File) iterator.next();
				ServiceItem item = toServiceItem(new FileInputStream(resourceFile));
				this.items.put(item.getId(), item);
			}
		} catch (IOException e) {
			LOG.errorf("Error on loading service file: %s", resourceFile);
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
		Jsonb jsonb = JsonbBuilder.create();
		ServiceItem result = jsonb.fromJson(is, ServiceItem.class);
		return result;
	}

}
