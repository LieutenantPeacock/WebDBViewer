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
package com.ltpeacock.dbviewer.commons;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author LieutenantPeacock
 */
public class DBConfig {
	public static class Database {
		@JacksonXmlProperty(isAttribute = true)
		private String name;
		@JacksonXmlProperty(localName = "url-format")
		private String urlFormat;
		@JacksonXmlProperty(localName = "pagination-format")
		private String paginationFormat;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrlFormat() {
			return urlFormat;
		}

		public void setUrlFormat(String urlFormat) {
			this.urlFormat = urlFormat;
		}

		public String getPaginationFormat() {
			return paginationFormat;
		}

		public void setPaginationFormat(String paginationFormat) {
			this.paginationFormat = paginationFormat;
		}

		@Override
		public String toString() {
			return "Database [name=" + name + ", urlFormat=" + urlFormat + ", paginationFormat=" + paginationFormat
					+ "]";
		}
	}

	private Map<String, Database> databases = new LinkedHashMap<>();
	@JacksonXmlProperty(localName = "default")
	private Database defaults;
	
	@JacksonXmlProperty(localName = "database")
	@JacksonXmlElementWrapper(useWrapping = false)
	public void setDatabases(final List<Database> databases) {
		for (final Database db : databases) {
			this.databases.put(db.name, db);
		}
	}

	public Map<String, Database> getDatabases() {
		return databases;
	}

	public Database getDefaults() {
		return defaults;
	}

	public void setDefaults(Database defaults) {
		this.defaults = defaults;
	}

	@Override
	public String toString() {
		return "DBConfig [databases=" + databases + ", defaults=" + defaults + "]";
	}
}
