package com.colonelparrot.dbviewer.config;

import java.util.List;
import java.util.Map;

/**
 * @author ColonelParrot
 * @version 1.0
 */
public class TableData {
	private List<String> columns;
	private List<Map<Object, Object>> rows;

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<Map<Object, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<Object, Object>> rows) {
		this.rows = rows;
	}

}
