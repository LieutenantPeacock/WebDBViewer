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
@ResponseStatus(HttpStatus.FORBIDDEN)
public class DBViewerAccessNotAllowedException extends DBViewerRuntimeException {
	private static final long serialVersionUID = -1530542336928845862L;

	public DBViewerAccessNotAllowedException() {
		this(ErrorCode.ACCESS_NOT_ALLOWED);
	}

	public DBViewerAccessNotAllowedException(final ErrorCode errorCode) {
		super(errorCode);
	}

	public DBViewerAccessNotAllowedException(final ErrorCode errorCode, final Throwable cause) {
		super(errorCode, cause);
	}

	public DBViewerAccessNotAllowedException(final ErrorCode errorCode, final String message) {
		super(errorCode, message);
	}

	public DBViewerAccessNotAllowedException(final ErrorCode errorCode, final String message, final Throwable cause) {
		super(errorCode, message, cause);
	}
}
