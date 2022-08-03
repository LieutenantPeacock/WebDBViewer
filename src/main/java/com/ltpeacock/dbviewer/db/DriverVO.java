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

/**
 * @author LieutenantPeacock
 */
public class DriverVO {
	private final String name;
	private final boolean deprecated;

	public DriverVO(final String name, final boolean deprecated) {
		this.name = name;
		this.deprecated = deprecated;
	}

	public String getName() {
		return name;
	}

	public boolean isDeprecated() {
		return deprecated;
	}
}
