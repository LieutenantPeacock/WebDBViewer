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
public class DBViewerRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 696292637801258554L;
	private final ErrorCode errorCode;

	public DBViewerRuntimeException(final ErrorCode errorCode) {
		this(errorCode, (Throwable) null);
	}

	public DBViewerRuntimeException(final ErrorCode errorCode, final Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public DBViewerRuntimeException(final ErrorCode errorCode, final String message) {
		this(errorCode, message, null);
	}

	public DBViewerRuntimeException(final ErrorCode errorCode, final String message, final Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
