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

import java.util.Objects;

/**
 * @author LieutenantPeacock
 */
public class DriverFromFile {
	private final String path;
	private final String className;

	public DriverFromFile(final String path, final String className) {
		this.path = path;
		this.className = className;
	}

	public String getPath() {
		return path;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public int hashCode() {
		return Objects.hash(className, path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DriverFromFile other = (DriverFromFile) obj;
		return Objects.equals(className, other.className) && Objects.equals(path, other.path);
	}
}
