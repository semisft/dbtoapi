package com.semiz.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ServiceCatalogFileStore implements ServiceCatalogStore {

	private static final Logger LOG = Logger.getLogger(ServiceCatalogFileStore.class);

	@ConfigProperty(name = "services.path")
	String servicesPath;

	@Override
	public List<ServiceItem> loadServices() {
		List<ServiceItem> result = new ArrayList<>(); 
		File resourceFile = null;
		try {
			List<File> resourceFiles = getResourceFiles(this.servicesPath);
			for (Iterator iterator = resourceFiles.iterator(); iterator.hasNext();) {
				resourceFile = (File) iterator.next();
				ServiceItem item = toServiceItem(new FileInputStream(resourceFile));
				result.add(item);
			}
		} catch (IOException e) {
			LOG.errorf("Error on loading service file: %s", resourceFile);
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
	

}
