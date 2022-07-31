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

/**
 * @author LieutenantPeacock
 */
public final class Constants {
	private Constants() {
		throw new UnsupportedOperationException();
	}
	public static final String CONFIG_FOLDER_PATH = "db-viewer-config";
	public static final String AUTH_PROPERTIES_NAME = "auth.properties";
	public static final String AUTH_PASSWORD_KEY = "password";
	public static final String AUTH_USERNAME_KEY = "username";
}
