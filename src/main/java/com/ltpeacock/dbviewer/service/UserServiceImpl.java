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

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.db.entity.AppUser;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;
import com.ltpeacock.dbviewer.form.SetupForm;

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

	@Override
	public AppUserPrincipal createAdmin(final SetupForm form, final BindingResult result) {
		if (!Objects.equals(form.getPassword(), form.getConfirmPassword()))
			result.rejectValue("password", "", "Password and confirm password must match.");
		if (!Objects.equals(form.getTempPassword(), setupInfo.getAdminPassword()))
			result.rejectValue("tempPassword", "", "Incorrect temporary password.");
		if(Objects.equals(form.getPassword(), setupInfo.getAdminPassword())) 
			result.rejectValue("password", "", "New password must be different from the temporary password.");
		if (!result.hasErrors()) {
			final AppUser user = new AppUser();
			user.setUsername(form.getUsername());
			user.setPassword(passwordEncoder.encode(form.getPassword()));
			return new AppUserPrincipal(userRepository.save(user));
		}
		return null;
	}
}
