package com.semiz.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.semiz.db.entity.SaveException;
import com.semiz.entity.DbConfig;
import com.semiz.entity.ServiceItem;

@ApplicationScoped
public class ServiceCatalogFileStore implements ServiceCatalogStore {

	private static final Logger LOG = Logger.getLogger(ServiceCatalogFileStore.class);

	private static final CharSequence DBCONFIG = "dbconfig";

	@ConfigProperty(name = "services.path")
	String servicesPath;

	@Override
	public List<ServiceItem> loadServices() {
		List<ServiceItem> result = new ArrayList<>();
		Map<Integer, DbConfig> dbConfigs = new HashMap<>();
		try {
			List<File> resourceFiles = getResourceFiles(this.servicesPath);
			resourceFiles.stream().filter(f -> f.getName().contains(DBCONFIG)).
			forEach(f ->
			{
				try {
					DbConfig dbConfig = DbConfig.toDbConfig(new FileInputStream(f));
					dbConfigs.put(dbConfig.getId(), dbConfig);
				} catch (FileNotFoundException e) {
					LOG.errorf("Error on loading service file: %s", f);
				}
			}
			);
			
			resourceFiles.stream().filter(f -> ! f.getName().contains(DBCONFIG)).
			forEach(f ->
			{
				ServiceItem item;
				try {
					item = ServiceItem.toServiceItem(new FileInputStream(f));
					item.setDbConfig(dbConfigs.get(item.getDbConfigId()));
					result.add(item);
				} catch (FileNotFoundException e) {
					LOG.errorf("Error on loading service file: %s", f);
				}
			}
			);
			
			
		} catch (Exception e) {
			LOG.errorf("Error on loading service from folder: %s, [%s]", this.servicesPath, e.getMessage());
		}
		return result;
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

	@Override
	public ServiceItem saveServiceItem(ServiceItem serviceItem) {
		//TODO:check path duplicates
		List<ServiceItem> services = loadServices();
		Integer maxId = Math.max(1, 1 + services.stream().map(i -> i.getId()).max(Integer::compareTo).get());
		serviceItem.setId(maxId);
		saveToFile(serviceItem);
		return serviceItem;
	}

	private void saveToFile(ServiceItem serviceItem) {
		String fileText = serviceItem.serviceItemToJson();
		try {
			FileUtils.writeStringToFile(new File(this.servicesPath + serviceItem.getId() + ".json"), fileText,
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new SaveException(serviceItem.getPath(), serviceItem.getId(), e.getMessage());
		}
	}


	@Override
	public ServiceItem updateServiceItem(ServiceItem serviceItem) {
		List<ServiceItem> services = loadServices();
		Optional<ServiceItem> currentService = services.stream().filter(i -> serviceItem.getId().equals(i.getId()))
				.findFirst();
		if (currentService.isPresent()) {
			saveToFile(serviceItem);
		}
		return serviceItem;
	}

}
