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
package com.ltpeacock.dbviewer.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ltpeacock.dbviewer.commons.AppProperties;
import com.ltpeacock.dbviewer.commons.DBConfig;

/**
 * @author LieutenantPeacock
 */
@Configuration
public class DatabasesConfig {
	private static final Logger LOG = LoggerFactory.getLogger(DatabasesConfig.class);

	@Bean
	public DBConfig dbConfig(final AppProperties appProps) throws IOException {
		final String dbConfigName = "dbconfig.xml";
		final File dbConfigFile = new File(appProps.getConfDir(), dbConfigName);
		if (!dbConfigFile.exists()) {
			LOG.info("No DB Config file found at [{}]; creating default one.", dbConfigFile.getAbsolutePath());
			FileUtils.copyInputStreamToFile(new ClassPathResource(dbConfigName).getInputStream(), dbConfigFile);
		}
		final XmlMapper xmlMapper = new XmlMapper();
		final DBConfig dbConfig = xmlMapper.readValue(dbConfigFile, DBConfig.class);
		LOG.info("{}", dbConfig);
		return dbConfig;
	}
}
