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
package com.ltpeacock.dbviewer.db;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ltpeacock.dbviewer.commons.exceptions.DBViewerRuntimeException;
import com.ltpeacock.dbviewer.commons.exceptions.ErrorCode;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;
import com.ltpeacock.dbviewer.service.DriversService;

/**
 * @author LieutenantPeacock
 */
@Component
public class CustomDriverManager {
	private static final Logger LOG = LoggerFactory.getLogger(CustomDriverManager.class);
	private final Map<DriverFromFile, Driver> drivers = new ConcurrentHashMap<>();
	@Autowired
	private DriversService driversService;

	public Connection getConnection(final DBConnectionDef connectionDef) {
		return getConnection(connectionDef.getDriverPath(), connectionDef.getDriverName(), connectionDef.getUrl(),
				connectionDef.getUsername(), connectionDef.getPassword());
	}

	public Connection getConnection(final String driverPath, final String driverClassName, final String url,
			final String username, final String password) {
		final Driver driver = drivers.computeIfAbsent(new DriverFromFile(driverPath, driverClassName), k -> {
			try (URLClassLoader ucl = this.driversService.getClassLoader(new File(driverPath))) {
				return (Driver) Class.forName(driverClassName, true, ucl).getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
					| ClassNotFoundException | IOException e) {
				LOG.error("Could not load Driver [{}] from path [{}]", driverClassName, driverPath);
				throw new DBViewerRuntimeException(ErrorCode.DRIVER_INITIALIZATION_ERROR, e);
			}
		});
		try {
			final Properties props = new Properties();
			props.put("user", username);
			props.put("password", password);
			return driver.connect(url, props);
		} catch (SQLException e) {
			throw new DBViewerRuntimeException(ErrorCode.CONNECTION_FAILED, e);
		}
	}
}
