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

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

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
	@Autowired
	private DBConnectionDefRepository dbConnectionDefRepository;
	@Autowired
	private AppUserRepository userRepository;

	@Override
	public MappedErrorsResponse<DBConnectionDefDTO> createConnection(final ConnectionForm form, final BindingResult result,
			final long userId) {
		if (!result.hasErrors()) {
			final DBConnectionDef connectionDef = new DBConnectionDef();
			connectionDef.setUrl(form.getUrl());
			connectionDef.setDriverPath(form.getDriverPath());
			connectionDef.setDriverName(form.getDriverName());
			connectionDef.setUsername(form.getUsername());
			connectionDef.setPassword(form.getPassword());
			connectionDef.setUsers(Collections.singletonList(this.userRepository.findById(userId)));
			return new MappedErrorsResponse<>(new DBConnectionDefDTO(this.dbConnectionDefRepository.save(connectionDef)));
		}
		return new MappedErrorsResponse<>(Util.toErrorMap(result));
	}
}
