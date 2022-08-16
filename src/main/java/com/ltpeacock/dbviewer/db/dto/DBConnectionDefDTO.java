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
package com.ltpeacock.dbviewer.db.dto;

import java.util.List;

import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;

/**
 * @author LieutenantPeacock
 */
public class DBConnectionDefDTO {
	private final long id;
	private final String name;
	private final String type;
	private final String url;
	private final String driverPath;
	private final String driverName;
	private final String username;
	private final String password;
	private List<AppUserDTO> users;

	public DBConnectionDefDTO(final DBConnectionDef def) {
		this.id = def.getId();
		this.name = def.getName();
		this.type = def.getType();
		this.url = def.getUrl();
		this.driverPath = def.getDriverPath();
		this.driverName = def.getDriverName();
		this.username = def.getUsername();
		this.password = def.getPassword();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getDriverPath() {
		return driverPath;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<AppUserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<AppUserDTO> users) {
		this.users = users;
	}
}
