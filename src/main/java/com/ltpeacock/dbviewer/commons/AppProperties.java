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
package com.ltpeacock.dbviewer.commons;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LieutenantPeacock
 */
@ConfigurationProperties(prefix = "dbviewer")
public class AppProperties {
	/**
	 * The root directory used to store application files. This can be a relative or absolute path.
	 */
	private String appRoot = ".dbviewer";
	/**
	 * The path to store the database file at, if using the default embedded H2 database.
	 * <br> <br>
	 * Default: <i>"./" + dbviewer.app-root + "/db"</i>
	 */
	private String dbDir = "./" + appRoot + "/db"; // implicit relative paths not allowed by H2
	/**
	 * The directory to store the database driver JARs in.
	 * <br> <br>
	 * Default: <i>dbviewer.app-root + "/ext/drivers"</i>
	 */
	private String driversDir = appRoot + "/ext/drivers";
	/**
	 * The temporary password to use on the first run to setup the admin user. 
	 * If not set, a random password will be generated.
	 */
	private String tempPassword;
	/**
	 * The directory to store the application configuration files.
	 * <br> <br>
	 * Default: <i>dbviewer.app-root + "/conf"</i>
	 */
	private String confDir = appRoot + "/conf";
	/**
	 * Whether or not to show raw exceptions with stracktraces to regular (non-admin) users.
	 */
	private boolean showRawExceptionsToUsers = false;
	
	public String getAppRoot() {
		return appRoot;
	}

	public void setAppRoot(String appRoot) {
		this.appRoot = appRoot;
	}

	public String getDbDir() {
		return dbDir;
	}

	public void setDbDir(String dbDir) {
		this.dbDir = dbDir;
	}

	public String getDriversDir() {
		return driversDir;
	}

	public void setDriversDir(String driversDir) {
		this.driversDir = driversDir;
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public String getConfDir() {
		return confDir;
	}

	public void setConfDir(String confDir) {
		this.confDir = confDir;
	}

	public boolean isShowRawExceptionsToUsers() {
		return showRawExceptionsToUsers;
	}

	public void setShowRawExceptionsToUsers(boolean showRawExceptionsToUsers) {
		this.showRawExceptionsToUsers = showRawExceptionsToUsers;
	}
}
