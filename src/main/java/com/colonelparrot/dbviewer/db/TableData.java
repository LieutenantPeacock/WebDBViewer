package com.colonelparrot.dbviewer.db;

import java.util.List;

import com.ltpeacock.dbviewer.db.TableColumn;

/**
 * @author ColonelParrot
 * @version 1.0
 */
public class TableData {
	private List<TableColumn> columns;
	private List<List<String>> rows;

	public TableData(final List<TableColumn> columns, final List<List<String>> rows) {
		this.columns = columns;
		this.rows = rows;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}
}
