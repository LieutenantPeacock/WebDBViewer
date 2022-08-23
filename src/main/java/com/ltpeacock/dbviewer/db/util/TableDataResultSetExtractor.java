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
package com.ltpeacock.dbviewer.db.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.colonelparrot.dbviewer.db.TableData;
import com.ltpeacock.dbviewer.db.DBConstants;
import com.ltpeacock.dbviewer.db.TableColumn;

/**
 * @author LieutenantPeacock
 */
@Component
public class TableDataResultSetExtractor implements ResultSetExtractor<TableData> {
	@Override
	public TableData extractData(final ResultSet rs) throws SQLException {
		return resultSetToTableData(rs);
	}

	public static TableData resultSetToTableData(final ResultSet rs) throws SQLException {
		final List<List<String>> rows = new ArrayList<>();
		final ResultSetMetaData meta = rs.getMetaData();
		final int colCount = meta.getColumnCount();
		final List<TableColumn> columns = new ArrayList<>(colCount);
		for (int col = 1; col <= colCount; col++) {
			columns.add(new TableColumn(meta.getColumnLabel(col), meta.getColumnTypeName(col),
					meta.getColumnDisplaySize(col)));
		}
		for (int i = 0; i < DBConstants.MAX_QUERY_RESULTS && rs.next(); i++) {
			final List<String> row = new ArrayList<>(colCount);
			for (int col = 1; col <= colCount; col++) {
				final Object value = rs.getObject(col);
				row.add(String.valueOf(value));
			}
			rows.add(row);
		}
		final TableData tableData = new TableData(columns, rows);
		if (rs.next()) {
			tableData.setMessage("Results truncated to " + DBConstants.MAX_QUERY_RESULTS + " rows.");
		}
		return tableData;
	}
}
