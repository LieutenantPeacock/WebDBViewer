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
public class TableColumn {
	private final String name;
	private final String typeName;
	private final int displaySize;
	private SortDirection dir = SortDirection.NONE;

	public TableColumn(final String name, final String typeName, final int displaySize) {
		this.name = name;
		this.typeName = typeName;
		this.displaySize = displaySize;
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public SortDirection getDir() {
		return dir;
	}

	public void setDir(SortDirection dir) {
		this.dir = dir;
	}
}
