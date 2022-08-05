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

import java.util.List;

import org.springframework.validation.BindingResult;

import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.db.dto.AppUserDTO;
import com.ltpeacock.dbviewer.db.entity.AppUser;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;
import com.ltpeacock.dbviewer.form.NewUserForm;
import com.ltpeacock.dbviewer.form.SetupForm;
import com.ltpeacock.dbviewer.response.MappedMultiErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;

/**
 * @author LieutenantPeacock
 */
public interface UserService {
	AppUserPrincipal createAdmin(SetupForm form, BindingResult result);
	List<DBConnectionDef> getConnections(long id);
	List<AppUser> getAllUsers();
	MappedMultiErrorsResponse<AppUserDTO> createUser(NewUserForm form, BindingResult result);
	List<AppUserDTO> findUsersNotInConnectionWithSimilarUsername(final long connectionId, 
			final String username);
	SimpleResponse<AppUserDTO> getUserInfo(final String username);
}
