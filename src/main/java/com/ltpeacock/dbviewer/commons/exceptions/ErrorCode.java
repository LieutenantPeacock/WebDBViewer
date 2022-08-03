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
package com.ltpeacock.dbviewer.commons.exceptions;

/**
 * @author LieutenantPeacock
 */
public enum ErrorCode {
	DRIVER_INITIALIZATION_ERROR("001", "Could not initialize Driver from JAR."),
	CONNECTION_FAILED("002", "Could not use specified Driver to connect to JDBC URL"),
	TABLES_RETRIEVAL_EXCEPTION("003", "Could not load tables in database."),
	ACCESS_NOT_ALLOWED("004", "Access to certain resource/endpoint is not allowed for the current user."),
	RESOURCE_NOT_FOUND("005", "Could not find resource."),
	TABLE_CONTENT_RETRIEVAL_EXCEPTION("006", "Could not load contents of table.");
	
	private final String code;
	private final String message;

	ErrorCode(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
