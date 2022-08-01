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

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ltpeacock.dbviewer.commons.AppProperties;

/**
 * @author LieutenantPeacock
 */
@Configuration
public class DataSourceConfig {
	private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

	@Bean
	public DataSource dataSource(final AppProperties appProps) {
		LOG.info("DataSourceConfig called");
		return DataSourceBuilder.create().url("jdbc:h2:file:" + appProps.getDbDir()).build();
	}
}
