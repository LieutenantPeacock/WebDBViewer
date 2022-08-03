package com.colonelparrot.dbviewer.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.colonelparrot.dbviewer.commons.exceptions.TableRetrievalException;
import com.colonelparrot.dbviewer.config.DatabaseData;
import com.colonelparrot.dbviewer.config.QueryResponse;
import com.colonelparrot.dbviewer.config.TableData;
import com.colonelparrot.dbviewer.db.ConnectionFactory;
import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.db.DriverVO;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
import com.ltpeacock.dbviewer.service.DBConnectionService;
import com.ltpeacock.dbviewer.service.DriversService;
import com.ltpeacock.dbviewer.service.UserService;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
public class ViewController {
	private static final String INVALID_FIELD_VALUE = "Invalid field value";
	private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private DriversService driversService;
	@Autowired
	private DBConnectionService dbConnectionService;

	@GetMapping("/")
	public String getStuff(final Model model, @AuthenticationPrincipal final AppUserPrincipal principal) {
		model.addAttribute("connections", userService.getConnections(principal.getId()));
		model.addAttribute("drivers", driversService.getDriverPaths());
		return "viewer";
	}
	
	@PostMapping("/uploadDriver")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public MappedErrorsResponse<String> uploadDriver(final MultipartFile[] files, @RequestParam final String folder) {
		LOG.info("Uploaded {} files", files.length);
		return this.driversService.uploadDriver(files, folder);
	}
	
	@GetMapping("/getDrivers")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public List<DriverVO> getDrivers(@RequestParam final String driverPath) throws IOException {
		return this.driversService.getDrivers(new File(driverPath));
	}
	
	@PostMapping("/newConnection")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public MappedErrorsResponse<DBConnectionDefDTO> newConnection(@Valid final ConnectionForm form, final BindingResult result, @AuthenticationPrincipal final AppUserPrincipal principal) {
		return this.dbConnectionService.createConnection(form, result, principal.getId());
	}

	@ResponseBody
	@PostMapping("/")
	public QueryResponse getData(@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "table", required = false) String table,
			@RequestParam(value = "database", required = false) String database, Model model, HttpSession session) {
		QueryResponse response = new QueryResponse();
		if (!isEmptyParam(type)) {
			if (type.equals("row")) {
				LOG.info("Table rows/data requested");
				/*
				 * Getting table rows/data
				 */
				if (!isEmptyParam(table, database)) {
					LOG.info("Received table [{}] and database [{}] values", table, database);
					int databaseIndex;
					int tableIndex;
					try {
						databaseIndex = Integer.parseInt(database);
						tableIndex = Integer.parseInt(table);
						LOG.info("Table and database values are numbers");
					} catch (NumberFormatException e) {
						LOG.info("Fail request, table and/or database value(s) is/are not a number");
						/*
						 * Has to be an index to prevent SQL injection
						 */
						failRequest(response, INVALID_FIELD_VALUE);
						return response;
					}
					try {
						List<String> databaseNames = getDatabases(ConnectionFactory.getConnection());
						if (LOG.isInfoEnabled())
							LOG.info("Retrieved database names [{}]", Arrays.toString(databaseNames.toArray()));
						int dbnamessize = databaseNames.size();
						if (databaseIndex >= dbnamessize || databaseIndex < 0) {
							LOG.info(
									"Fail request, database index is greater than the size of databases [{}] or is smaller than 0",
									dbnamessize);
							failRequest(response, INVALID_FIELD_VALUE);
						} else {
							String databaseName = databaseNames.get(databaseIndex);
							List<String> tableNames = getTables(databaseName);
							if (tableIndex >= tableNames.size() || tableIndex < 0) {
								LOG.info(
										"Fail request, table index is greater than the table name size of [{}] or is smaller than 0",
										dbnamessize);
								failRequest(response, INVALID_FIELD_VALUE);
							} else {
								String tableName = tableNames.get(tableIndex);
								LOG.info("Retrieved table name [{}]", tableName);
								ResultSet rs = getTableContentResultSet(tableName, databaseName);
								TableData convertedResultSet = resultSetToTableData(rs);
								LOG.info(
										"Converted ResultSet from getTableContentResultSet() to TableData object, setting success to true and returning response");
								response.setSuccess(true);
								response.setData(convertedResultSet);
							}
						}

					} catch (Exception e) {
						LOG.error("Exception", e);
						failRequest(response, "An error occured. Contact your administrator if this error persists");
					}
				} else {
					LOG.info("Fail request, able index or database index is missing");
					failRequest(response, "Please fill all required fields");
				}
			} else if (type.equals("table")) {
				LOG.info("Tables in database requested");
				/*
				 * Geting tables in database
				 */
				if (!isEmptyParam(database)) {
					LOG.info("Received database parameter [{}]", database);
					int databaseIndex;
					try {
						databaseIndex = Integer.parseInt(database);
					} catch (NumberFormatException e) {
						LOG.info("Fail request, database parameter is not a number");
						/*
						 * Has to be an index to prevent SQL injection
						 */
						failRequest(response, INVALID_FIELD_VALUE);
						return response;
					}
					try {
						List<String> databaseNames = getDatabases(ConnectionFactory.getConnection());
						int dbnamessize = databaseNames.size();
						if (LOG.isInfoEnabled())
							LOG.info("Retrieved database names [{}]", Arrays.toString(databaseNames.toArray()));
						if (databaseIndex >= dbnamessize || databaseIndex < 0) {
							LOG.info(
									"Fail request, database index is greater than the size of databases [{}] or is smaller than 0",
									dbnamessize);
							failRequest(response, INVALID_FIELD_VALUE);
						} else {
							String databaseName = databaseNames.get(databaseIndex);
							LOG.info("Retrieved database name [{}]", databaseName);
							DatabaseData databaseData = new DatabaseData();
							List<String> tableNames = getTables(databaseName);
							LOG.info("Retrieved table names [{}]", Arrays.toString(tableNames.toArray()));
							databaseData.setTableNames(tableNames);
							response.setSuccess(true);
							response.setData(databaseData);
						}
					} catch (SQLException e) {
						LOG.error("SQLException", e);
						failRequest(response, "An error occured. Contact your administrator if this error persists");
					}
				} else {
					LOG.info("Fail request, database parameter is missing");
					failRequest(response, "Please fill all required fields");
				}
			}
		} else {
			LOG.info("No type parameter specified in ");
			failRequest(response, "No response type set");
		}
		return response;
	}

	public void failRequest(QueryResponse response, String message) {
		response.setSuccess(false);
		response.setMessage(message);
	}

	public TableData resultSetToTableData(ResultSet rs) throws SQLException {
		TableData tableData = new TableData();
		List<Map<Object, Object>> t = new ArrayList<>();
		List<String> columns = new ArrayList<>();
		boolean isFirst = true;
		while (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();
			Map<Object, Object> m = new HashMap<>();
			for (int col = 1; col <= colCount; col++) {
				/*
				 * Loop through all columns
				 */
				Object value = rs.getObject(col);
				if (value != null) {
					final String columnName = meta.getColumnName(col);
					if (isFirst) {
						/*
						 * If it's the first loop cycle, also add to columns list to spare 1 loop cycle
						 */
						columns.add(columnName);
					}
					final String columnValue = String.valueOf(rs.getObject(col));
					m.put(columnName, columnValue);
				}
			}
			if (isFirst) {
				isFirst = false;
			}
			t.add(m);
		}
		tableData.setRows(t);
		tableData.setColumns(columns);
		return tableData;

	}

	public static boolean isEmptyParam(final String... params) {
		for (String param : params) {
			if (null == param || param.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public ResultSet getTableContentResultSet(String tableName, String databaseName) throws TableRetrievalException {
		try (Connection c = ConnectionFactory.getConnection(databaseName);
				PreparedStatement stmt = c.prepareStatement("select * from " + tableName)) {
			ResultSet result = stmt.executeQuery();
			return result;
		} catch (Exception e) {
			LOG.error("getTableData ERROR: [{}]", e);
			throw new TableRetrievalException();
		}
	}

	public List<String> getTables(String databaseName) throws SQLException {
		List<String> tableNames = new ArrayList<>();
		Connection con = ConnectionFactory.getConnection(databaseName);
		DatabaseMetaData md = con.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
			tableNames.add(rs.getString(3));
		}
		return tableNames;
	}

	public List<String> getDatabases(Connection c) throws SQLException {
		DatabaseMetaData metaData = c.getMetaData();
		ResultSet res = metaData.getCatalogs();
		List<String> databaseNames = new ArrayList<>();
		while (res.next()) {
			databaseNames.add(res.getString("TABLE_CAT"));
		}
		return databaseNames;
	}
}
