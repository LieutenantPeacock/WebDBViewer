package com.colonelparrot.dbviewer.config;

import java.util.List;

/**
 * @author ColonelParrot
 * @version 1.0
 */
public class TableData {
	private List<String> columnNames;
	private List<List<String>> rows;
	
	public TableData(final List<String> columnNames, final List<List<String>> rows) {
		this.columnNames = columnNames;
		this.rows = rows;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnsNames) {
		this.columnNames = columnsNames;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}
}
