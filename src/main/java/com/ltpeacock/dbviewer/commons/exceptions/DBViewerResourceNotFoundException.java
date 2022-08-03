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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author LieutenantPeacock
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DBViewerResourceNotFoundException extends DBViewerRuntimeException {
	private static final long serialVersionUID = -8605570575354477694L;

	public DBViewerResourceNotFoundException() {
		this(ErrorCode.RESOURCE_NOT_FOUND);
	}

	public DBViewerResourceNotFoundException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public DBViewerResourceNotFoundException(final ErrorCode errorCode, final Throwable cause) {
		super(errorCode, cause);
	}

	public DBViewerResourceNotFoundException(final ErrorCode errorCode, final String message) {
		super(errorCode, message);
	}

	public DBViewerResourceNotFoundException(final ErrorCode errorCode, final String message, final Throwable cause) {
		super(errorCode, message, cause);
	}
}
