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

import com.colonelparrot.dbviewer.db.TableData;

/**
 * @author LieutenantPeacock
 */
public class QueryResult {
	private final TableData results;
	private final int updateCount;

	private QueryResult(final TableData results, final int updateCount) {
		this.results = results;
		this.updateCount = updateCount;
	}

	public QueryResult(final int updateCount) {
		this(null, updateCount);
	}

	public QueryResult(final TableData results) {
		this(results, -1);
	}

	public TableData getResults() {
		return results;
	}

	public int getUpdateCount() {
		return updateCount;
	}
}
