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
	private int totalRows;
	private String message;

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

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
