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
package com.ltpeacock.dbviewer.response;

/**
 * @author LieutenantPeacock
 */
public class SimpleResponse<T> {
	private T value;
	private String errorMessage;

	public SimpleResponse(final T value) {
		this.value = value;
	}

	public SimpleResponse(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public T getValue() {
		return value;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static <T> SimpleResponse<T> withValue(final T value) {
		return new SimpleResponse<>(value);
	}
	
	public static <T> SimpleResponse<T> withErrorMessage(final String errorMessage) {
		return new SimpleResponse<>(errorMessage);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> SimpleResponse<T> of(final SimpleResponse<?> res) {
		return (SimpleResponse<T>) res;
	}
}
