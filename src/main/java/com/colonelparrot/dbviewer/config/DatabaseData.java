package com.colonelparrot.dbviewer.config;

import java.util.List;

/**
 * @author ColonelParrot
 * @version 1.1
 */
public class DatabaseData {
	public List<String> tableNames;

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}
}
