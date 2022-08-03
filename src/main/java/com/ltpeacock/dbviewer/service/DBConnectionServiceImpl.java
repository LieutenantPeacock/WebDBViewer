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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.colonelparrot.dbviewer.db.TableData;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerAccessNotAllowedException;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerResourceNotFoundException;
import com.ltpeacock.dbviewer.commons.exceptions.DBViewerRuntimeException;
import com.ltpeacock.dbviewer.commons.exceptions.ErrorCode;
import com.ltpeacock.dbviewer.db.CustomDriverManager;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;
import com.ltpeacock.dbviewer.db.repository.DBConnectionDefRepository;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
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

	@Override
	public MappedErrorsResponse<DBConnectionDefDTO> createConnection(final ConnectionForm form,
			final BindingResult result, final long userId) {
		if (!result.hasErrors()) {
			final DBConnectionDef connectionDef = new DBConnectionDef();
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
	public TableData getTableContents(final long connectionId, final long userId, final String tableName) {
		final DBConnectionDef def = getDef(connectionId, userId);
		try (Connection con = driverManager.getConnection(def)) {
			final DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getTables(con.getCatalog(), con.getSchema(), tableName, new String[] { "TABLE" })) {
				if (!rs.next())
					throw new DBViewerResourceNotFoundException();
			}
			final String quote = md.getIdentifierQuoteString();
			try (Statement statement = con.createStatement();
					ResultSet rs = statement.executeQuery("select * from " + quote + tableName + quote)) {
				return resultSetToTableData(rs);
			}
		} catch (SQLException e) {
			throw new DBViewerRuntimeException(ErrorCode.TABLE_CONTENT_RETRIEVAL_EXCEPTION, e);
		}
	}

	private TableData resultSetToTableData(final ResultSet rs) throws SQLException {
		final List<List<String>> rows = new ArrayList<>();
		final ResultSetMetaData meta = rs.getMetaData();
		final int colCount = meta.getColumnCount();
		final List<String> columnNames = new ArrayList<>(colCount);
		for(int col = 1; col <= colCount; col++)
			columnNames.add(meta.getColumnName(col));
		while (rs.next()) {
			final List<String> row = new ArrayList<>(colCount);
			for (int col = 1; col <= colCount; col++) {
				final Object value = rs.getObject(col);
				row.add(String.valueOf(value));
			}
			rows.add(row);
		}
		return new TableData(columnNames, rows);
	}
}
