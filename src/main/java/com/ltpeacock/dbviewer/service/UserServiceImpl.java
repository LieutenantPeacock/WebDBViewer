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
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.commons.RoleNames;
import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.db.dto.AppUserDTO;
import com.ltpeacock.dbviewer.db.dto.NamedQueryDTO;
import com.ltpeacock.dbviewer.db.entity.AppUser;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;
import com.ltpeacock.dbviewer.db.repository.DBConnectionDefRepository;
import com.ltpeacock.dbviewer.form.NewUserForm;
import com.ltpeacock.dbviewer.form.SetupForm;
import com.ltpeacock.dbviewer.response.MappedMultiErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;
import com.ltpeacock.dbviewer.util.Util;

/**
 * @author LieutenantPeacock
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private AppUserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SetupInfo setupInfo;
	@Autowired
	private DBConnectionDefRepository dbConnectionDefRepository;

	@Override
	public AppUserPrincipal createAdmin(final SetupForm form, final BindingResult result) {
		if (!Objects.equals(form.getPassword(), form.getConfirmPassword()))
			result.rejectValue("password", "", "Password and confirm password must match.");
		if (!Objects.equals(form.getTempPassword(), setupInfo.getAdminPassword()))
			result.rejectValue("tempPassword", "", "Incorrect temporary password.");
		if (Objects.equals(form.getPassword(), setupInfo.getAdminPassword()))
			result.rejectValue("password", "", "New password must be different from the temporary password.");
		if (!result.hasErrors()) {
			final AppUser user = new AppUser();
			user.setUsername(form.getUsername());
			user.setPassword(passwordEncoder.encode(form.getPassword()));
			user.addAuthority(RoleNames.ADMIN);
			return new AppUserPrincipal(userRepository.save(user));
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public List<DBConnectionDef> getConnections(final long id) {
		final AppUser user = userRepository.findById(id);
		user.getConnections().size(); // trigger initialization
		return user.getConnections();
	}

	@Override
	public List<AppUser> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public MappedMultiErrorsResponse<AppUserDTO> createUser(final NewUserForm form, final BindingResult result) {
		if (!Objects.equals(form.getPassword(), form.getConfirmPassword()))
			result.rejectValue("confirmPassword", "", "Password and confirm password must match.");
		if (userRepository.findByUsernameIgnoreCase(form.getUsername()) != null) 
			result.rejectValue("username", "", "Username already exists.");
		if (!result.hasErrors()) {
			final AppUser user = new AppUser();
			user.setUsername(form.getUsername());
			user.setPassword(passwordEncoder.encode(form.getPassword()));
			return new MappedMultiErrorsResponse<>(new AppUserDTO(userRepository.save(user)));
		}
		return new MappedMultiErrorsResponse<>(Util.toMultiErrorMap(result));
	}
	
	@Override
	public List<AppUserDTO> findUsersNotInConnectionWithSimilarUsername(long connectionId, final String username) {
		final DBConnectionDef connectionDef = dbConnectionDefRepository.getReferenceById(connectionId);
		return userRepository.findUsersNotInConnectionWithSimilarUsername(connectionDef, username)
				.stream().map(AppUserDTO::new).collect(Collectors.toList());
	}
	
	@Override
	public SimpleResponse<AppUserDTO> getUserInfo(final String username) {
		final AppUser user = userRepository.findByUsernameIgnoreCase(username);
		return user != null ? new SimpleResponse<>(new AppUserDTO(user)) : 
				new SimpleResponse<>("User does not exist.");
	}

	@Transactional(readOnly = true)
	@Override
	public List<NamedQueryDTO> getQueries(final long id) {
		return userRepository.findById(id).getQueries().stream().map(NamedQueryDTO::new).collect(Collectors.toList());
	}
}
