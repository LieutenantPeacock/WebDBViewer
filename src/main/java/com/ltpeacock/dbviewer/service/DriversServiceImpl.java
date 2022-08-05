/**
 *  Copyright (C) 2022  LieutenantPeacock
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package com.ltpeacock.dbviewer.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ltpeacock.dbviewer.commons.AppProperties;
import com.ltpeacock.dbviewer.db.DriverVO;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;

/**
 * @author LieutenantPeacock
 */
@Service
public class DriversServiceImpl implements DriversService {
	private static final Logger LOG = LoggerFactory.getLogger(DriversServiceImpl.class);
	@Autowired
	private AppProperties appProps;
	private File driversDir;
	private List<String> driverPaths = Collections.emptyList();
	private final FileFilter jarFilter = file -> file.getName().endsWith(".jar");

	@PostConstruct
	private void setup() {
		driversDir = new File(appProps.getDriversDir());
		if (!driversDir.exists()) {
			LOG.info("No drivers directory found at [{}]; creating one now.", driversDir.getAbsolutePath());
			if (!driversDir.mkdirs())
				LOG.error("Could not create drivers directory!");
		}
		this.refreshDrivers();
	}

	@Override
	public List<String> getDriverPaths() {
		return this.driverPaths;
	}

	@Override
	public void refreshDrivers() {
		driverPaths.clear();
		final File[] files = driversDir
				.listFiles(file -> file.isFile() ? jarFilter.accept(file) : file.listFiles(jarFilter).length > 0);
		driverPaths = Arrays.stream(files).map(this::generatePath).collect(Collectors.toList());
	}

	@Override
	public MappedErrorsResponse<String> uploadDriver(final MultipartFile[] files, final String folder) {
		final Map<String, String> errors = new HashMap<>();
		String driverPath = null;
		if (files == null || files.length == 0)
			errors.put("driverUpload", "No files uploaded.");
		else if (files.length > 1 && StringUtils.isBlank(folder))
			errors.put("driverFolderName", "Folder name is required when more than one file is uploaded.");
		if (errors.isEmpty()) {
			final File dir = StringUtils.isBlank(folder) ? driversDir
					: new File(driversDir, FilenameUtils.getName(folder));
			if (dir != driversDir) {
				if (dir.exists()) {
					LOG.warn("Uploading files into existing directory [{}]. Previous contents will be cleared.",
							dir.getAbsolutePath());
					try {
						FileUtils.cleanDirectory(dir);
					} catch (IOException e) {
						LOG.error("IOException", e);
					}
				} else {
					dir.mkdirs();
				}
				driverPath = this.generatePath(dir);
			} else {
				driverPath = this.generatePath(FilenameUtils.getName(files[0].getOriginalFilename()));
			}
			this.driverPaths.add(driverPath);
			for (final MultipartFile file : files)
				try (InputStream is = file.getInputStream()) {
					final File output = new File(dir, FilenameUtils.getName(file.getOriginalFilename()));
					if (output.exists())
						LOG.warn("Overwriting file [{}]", output.getAbsolutePath());
					IOUtils.copy(is, new FileOutputStream(output));
				} catch (IOException e) {
					LOG.error("IOException", e);
					errors.put("driverUpload", "Error uploading file with name [" + file.getOriginalFilename() + "].");
					break;
				}
		}
		return errors.isEmpty() ? new MappedErrorsResponse<>(driverPath)
				: new MappedErrorsResponse<>(errors);
	}

	@Override
	public URLClassLoader getClassLoader(final File file) throws IOException {
		return getClassLoader(getJARs(file));
	}
	
	private URLClassLoader getClassLoader(final File[] files) throws MalformedURLException {
		final URL[] urls = new URL[files.length];
		for (int i = 0; i < urls.length; i++)
			urls[i] = files[i].toURI().toURL();
		return new URLClassLoader(urls);
	}
	
	private File[] getJARs(final File file) {
		return file.isDirectory() ? file.listFiles(jarFilter) : new File[] { file };
	}
	
	@Override
	public List<DriverVO> getDrivers(final File file) throws IOException {
		final File[] files = getJARs(file);
		final List<DriverVO> drivers = new ArrayList<>();
		try (final URLClassLoader ucl = getClassLoader(files)) {
			for (final File f : files) {
				try (final JarFile jar = new JarFile(f)) {
					jar.stream().forEach(entry -> {
						final String name = entry.getName();
						if (name.endsWith(".class") && !name.contains("$") && !name.startsWith("META-INF")) {
							final String normalizedName = name.replace('/', '.').substring(0, name.length() - 6);
							try {
								Class<?> clazz = Class.forName(normalizedName, false, ucl);
								if (Driver.class.isAssignableFrom(clazz)) {
									LOG.info("Found driver [{}]", clazz.getCanonicalName());
									drivers.add(new DriverVO(clazz.getCanonicalName(),
											clazz.isAnnotationPresent(Deprecated.class)));
								}
							} catch (Throwable t) {
								LOG.info("Error [{}] loading class [{}]", t.getMessage(), name);
							}
						}
					});
				}
			}
		}
		return drivers;
	}

	private String generatePath(final File file) {
		return generatePath(file.getName());
	}

	private String generatePath(final String name) {
		return appProps.getDriversDir() + "/" + name;
	}
}
