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

import java.util.Collections;
import java.util.Map;

/**
 * @author LieutenantPeacock
 */
public class MappedErrorsResponse<T> {
	private T value;
	private Map<String, String> errors = Collections.emptyMap();

	public MappedErrorsResponse(final T value) {
		this.value = value;
	}

	public MappedErrorsResponse(final Map<String, String> errors) {
		this.errors = errors;
	}
	
	public MappedErrorsResponse(final T value, final Map<String, String> errors) {
		this.value = value;
		this.errors = errors;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
	
	public boolean getSuccess() {
		return this.errors.isEmpty();
	}
}
