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
package com.ltpeacock.dbviewer.service;

import static com.ltpeacock.dbviewer.commons.LogMarkers.AUDIT;
import static com.ltpeacock.dbviewer.commons.MDCKeys.CONNECTION_ID;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.colonelparrot.dbviewer.db.TableData;
import com.ltpeacock.dbviewer.commons.DBConfig;
import com.ltpeacock.dbviewer.commons.RoleNames;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerAccessNotAllowedException;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerResourceNotFoundException;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerRuntimeException;
import com.ltpeacock.dbviewer.commons.exceptions.ErrorCode;
import com.ltpeacock.dbviewer.db.CustomDriverManager;
import com.ltpeacock.dbviewer.db.DBConstants;
import com.ltpeacock.dbviewer.db.QueryResult;
import com.ltpeacock.dbviewer.db.SortDirection;
import com.ltpeacock.dbviewer.db.dto.AppUserDTO;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;
import com.ltpeacock.dbviewer.db.repository.DBConnectionDefRepository;
import com.ltpeacock.dbviewer.db.util.TableDataResultSetExtractor;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
import com.ltpeacock.dbviewer.response.MappedMultiErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;
import com.ltpeacock.dbviewer.util.Util;

/**
 * @author LieutenantPeacock
 */
@Service
public class DBConnectionServiceImpl implements DBConnectionService {
	private static final Logger LOG = LoggerFactory.getLogger(DBConnectionServiceImpl.class);
	@Autowired
	private DBConnectionDefRepository dbConnectionDefRepository;
	@Autowired
	private AppUserRepository userRepository;
	@Autowired
	private CustomDriverManager driverManager;
	@Autowired
	private DriversService driversService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private DBConfig dbConfig;
	@Autowired
	private TableDataResultSetExtractor tableDataResultSetExtractor;

	@Override
	public MappedErrorsResponse<DBConnectionDefDTO> createConnection(final ConnectionForm form,
			final BindingResult result, final long userId) {
		if (!result.hasErrors()) {
			final DBConnectionDef connectionDef = new DBConnectionDef();
			connectionDef.setName(form.getName());
			connectionDef.setType(form.getType());
			connectionDef.setUrl(form.getUrl());
			connectionDef.setDriverPath(form.getDriverPath());
			connectionDef.setDriverName(form.getDriverName());
			connectionDef.setUsername(form.getUsername());
			connectionDef.setPassword(form.getPassword());
			connectionDef.setUsers(Collections.singletonList(this.userRepository.findById(userId)));
			return new MappedErrorsResponse<>(
					new DBConnectionDefDTO(this.dbConnectionDefRepository.save(connectionDef)));
		}
		return new MappedErrorsResponse<>(Util.toErrorMap(result));
	}

	private DBConnectionDef getDef(final long connectionId, final long userId) {
		final DBConnectionDef def = this.dbConnectionDefRepository.findById(connectionId);
		if (def == null)
			throw new DBViewerResourceNotFoundException();
		if (def.getUsers().stream().noneMatch(user -> user.getId() == userId))
			throw new DBViewerAccessNotAllowedException();
		return def;
	}

	@Transactional
	@Override
	public List<String> getTables(final long connectionId, final long userId) {
		final DBConnectionDef def = getDef(connectionId, userId);
		final List<String> tableNames = new ArrayList<>();
		try (Connection con = driverManager.getConnection(def);) {
			final DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getTables(con.getCatalog(), con.getSchema(), "%", new String[] { "TABLE" });) {
				while (rs.next()) {
					tableNames.add(rs.getString(3));
				}
			}
			return tableNames;
		} catch (SQLException e) {
			throw new DBViewerRuntimeException(ErrorCode.TABLES_RETRIEVAL_EXCEPTION, e);
		}
	}

	@Transactional
	@Override
	public SimpleResponse<TableData> getTableContents(final long connectionId, final long userId, final String tableName,
			final int page, final String sortColumn, final SortDirection dir, final String where) {
		final DBConnectionDef def = getDef(connectionId, userId);
		try (Connection con = driverManager.getConnection(def)) {
			final DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getTables(con.getCatalog(), con.getSchema(), tableName, new String[] { "TABLE" })) {
				if (!rs.next())
					throw new DBViewerResourceNotFoundException();
			}
			final String quote = md.getIdentifierQuoteString();
			String orderBy = "";
			int sortColPos = -1;
			if (sortColumn == null || dir == null || dir == SortDirection.NONE) {
				try (ResultSet rs = md.getPrimaryKeys(con.getCatalog(), con.getSchema(), tableName)) {
					StringBuilder keys = new StringBuilder();
					while (rs.next()) {
						if (keys.length() != 0) keys.append(',');
						keys.append(quote + rs.getString("COLUMN_NAME") + quote);
					}
					if (keys.length() != 0)
						orderBy = " ORDER BY " + keys.toString();
				}
			} else {
				try (ResultSet rs = md.getColumns(con.getCatalog(), con.getSchema(), tableName, sortColumn)) {
					if (!rs.next())
						throw new DBViewerResourceNotFoundException();
					sortColPos = rs.getInt("ORDINAL_POSITION") - 1;
				}
				orderBy = " ORDER BY " + quote + sortColumn + quote + " " + dir.toString();
			}
			final int offset = (page - 1) * DBConstants.PAGE_SIZE;
			final String mainPart = quote + tableName + quote 
					+ (StringUtils.isBlank(where) ? "" : " where " + where);
			final String paginationFormat = Optional.ofNullable(dbConfig.getDatabases().get(def.getType()))
					.map(DBConfig.Database::getPaginationFormat)
					.orElseGet(() -> dbConfig.getDefaults().getPaginationFormat());
			final String query = "SELECT * FROM " + 
					mainPart + orderBy + " " + 
					paginationFormat.replace("$offset", String.valueOf(offset))
									.replace("$pagesize", String.valueOf(DBConstants.PAGE_SIZE));
			LOG.debug("Query [{}]", query);
			try (Statement statement = con.createStatement();) {
				final SimpleResponse<TableData> tableDataRes = getQueryResult(statement, query, 
						tableDataResultSetExtractor);
				if (tableDataRes.getErrorMessage() != null)
					return tableDataRes;
				final TableData tableData = tableDataRes.getValue();
				if (sortColPos != -1)
					tableData.getColumns().get(sortColPos).setDir(dir);
				final String countQuery = "SELECT COUNT(*) FROM " + mainPart;
				LOG.debug("Count query [{}]", countQuery);
				final SimpleResponse<Integer> rowCountRes = getQueryResult(statement, countQuery,
						rs -> {
							rs.next();
							return rs.getInt(1);
						});
				if (rowCountRes.getErrorMessage() != null)
					return SimpleResponse.of(rowCountRes);
				tableData.setTotalRows(rowCountRes.getValue());
				return new SimpleResponse<>(tableData);
			}
		} catch (SQLException e) {
			LOG.warn("SQLException [{}]", Util.toOneLine(e));
			return new SimpleResponse<>(e.getMessage());
		}
	}
	
	private <T> SimpleResponse<T> getQueryResult(final Statement statement, final String query,
			final ResultSetExtractor<T> extractor) {
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(query);
			return SimpleResponse.withValue(extractor.extractData(rs));
		} catch (SQLException e) {
			LOG.error("SQLException executing query [{}]: [{}]", query, Util.toOneLine(e));
			return SimpleResponse.withErrorMessage(e.getMessage());
		} finally {
			Util.closeOrWarn(rs);
		}
	}
		
	@Transactional
	@Override
	public SimpleResponse<QueryResult> executeQuery(final long connectionId, final long userId,
			final String query) {
		MDC.put(CONNECTION_ID, String.valueOf(connectionId));
		final DBConnectionDef def = getDef(connectionId, userId);
		if (StringUtils.countMatches(query, ';') > 1) {
			LOG.info(AUDIT, "Attempt to execute multiple statements denied.");
			return new SimpleResponse<>("Only one statement allowed at a time.");
		}
		final StopWatch sw = StopWatch.createStarted();
		boolean success = false;
		try (Connection con = driverManager.getConnection(def);
				Statement statement = con.createStatement()) {
			final boolean isResultSet = statement.execute(query);
			final SimpleResponse<QueryResult> response = isResultSet ? 
					new SimpleResponse<>(new QueryResult(TableDataResultSetExtractor.resultSetToTableData(statement.getResultSet())))
					: new SimpleResponse<>(new QueryResult(statement.getUpdateCount()));
			success = true;
			return response;
		} catch (SQLException e) {
			LOG.warn("SQLException [{}]", Util.toOneLine(e));
			return new SimpleResponse<>(e.getMessage());
		} finally {
			sw.stop();
			LOG.info(AUDIT,
				"Query [{}] {} in {} ms", query, success ? "succeeded" : "failed", sw.getTime());
			MDC.remove(CONNECTION_ID);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public DBConnectionDefDTO getConnectionDetails(final long id) {
		final DBConnectionDef connectionDef = dbConnectionDefRepository.findById(id);
		final DBConnectionDefDTO connectionDefDTO = new DBConnectionDefDTO(connectionDef);
		connectionDefDTO.setUsers(connectionDef.getUsers().stream().map(AppUserDTO::new)
					.collect(Collectors.toList()));
		return connectionDefDTO;
	}
	
	@Transactional
	@Override
	public MappedMultiErrorsResponse<DBConnectionDefDTO> updateConnection(final ConnectionForm form,
			final BindingResult result) {
		if (result.hasErrors()) {
			return new MappedMultiErrorsResponse<>(Util.toMultiErrorMap(result));
		}
		final DBConnectionDef connectionDef = dbConnectionDefRepository.getReferenceById(form.getId());
		BeanUtils.copyProperties(form, connectionDef, "id", "userIds");
		connectionDef.setUsers(userRepository.findAllById(form.getUserIds()));
		return new MappedMultiErrorsResponse<>(new DBConnectionDefDTO(dbConnectionDefRepository.save(connectionDef)));
	}

	@Override
	public SimpleResponse<String> testConnection(final ConnectionForm form, final long userId) {
		if (StringUtils.isBlank(form.getUrl()))
			return SimpleResponse.withErrorMessage("JDBC URL is blank!");
		if (StringUtils.isBlank(form.getDriverPath()) || StringUtils.isBlank(form.getDriverName()))
			return SimpleResponse.withErrorMessage("Please choose the JDBC Driver!");
		if (!request.isUserInRole(RoleNames.ADMIN)) {
			final DBConnectionDef connectionDef = getDef(form.getId(), userId);
			BeanUtils.copyProperties(connectionDef, form, "users");
		}
		try {
			final ClassLoader loader = driversService.getClassLoader(new File(form.getDriverPath()));
			final Driver driver = (Driver) Class.forName(form.getDriverName(), true, loader).getDeclaredConstructor().newInstance();
			final Properties props = new Properties();
			props.put("user", form.getUsername());
			props.put("password", form.getPassword());
			driver.connect(form.getUrl(), props);
			return SimpleResponse.withValue("Successfully connected!");
		} catch (IOException e) {
			return SimpleResponse.withErrorMessage("Could not get ClassLoader for JAR: " + e.getMessage());
		}  catch (InstantiationException | IllegalAccessException  | InvocationTargetException
				| NoSuchMethodException | ClassNotFoundException e) {
			return SimpleResponse.withErrorMessage("Could not load driver from JAR: " + e.getMessage());
		} catch (SQLException e) {
			return SimpleResponse.withErrorMessage("Could not connect: " + e.getMessage());
		}
	}
}
