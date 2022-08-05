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

import java.util.List;
import java.util.Map;

/**
 * @author LieutenantPeacock
 */
public class MappedMultiErrorsResponse<T> {
	private T value;
	private Map<String, List<String>> errors;

	public MappedMultiErrorsResponse(final T value) {
		this.value = value;
	}

	public MappedMultiErrorsResponse(final Map<String, List<String>> errors) {
		this.errors = errors;
	}

	public T getValue() {
		return value;
	}

	public Map<String, List<String>> getErrors() {
		return errors;
	}
}
