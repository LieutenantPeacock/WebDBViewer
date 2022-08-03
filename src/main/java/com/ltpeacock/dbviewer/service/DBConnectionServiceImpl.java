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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.ltpeacock.dbviewer.commons.exceptions.DBViewerAccessNotAllowedException;
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

	@Transactional
	@Override
	public List<String> getTables(final long connectionId, final long userId) {
		final DBConnectionDef def = this.dbConnectionDefRepository.findById(connectionId);
		if (def == null)
			return null;
		if (def.getUsers().stream().noneMatch(user -> user.getId() == userId))
			throw new DBViewerAccessNotAllowedException();
		final List<String> tableNames = new ArrayList<>();
		try (Connection con = driverManager.getConnection(def);) {
			final DatabaseMetaData md = con.getMetaData();
			try (ResultSet rs = md.getTables(con.getCatalog(), con.getSchema(), "%", new String[] {"TABLE"});) {
				while (rs.next()) {
					tableNames.add(rs.getString(3));
				}
			}
			return tableNames;
		} catch (SQLException e) {
			throw new DBViewerRuntimeException(ErrorCode.TABLES_RETRIEVAL_EXCEPTION, e);
		}
	}
}
