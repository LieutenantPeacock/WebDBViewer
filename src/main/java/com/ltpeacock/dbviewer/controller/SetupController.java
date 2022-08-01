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
package com.ltpeacock.dbviewer.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.form.SetupForm;
import com.ltpeacock.dbviewer.service.UserService;

/**
 * @author LieutenantPeacock
 */
@Controller
public class SetupController {
	@Autowired
	private SetupInfo setupInfo;
	@Autowired
	private UserService userService;

	@GetMapping("/setup")
	public String showSetup(final SetupForm setupForm) {
		return setupInfo.getAdminPassword() != null ? "setup" : "redirect:/";
	}

	@PostMapping("/setup")
	public String doSetup(@Valid final SetupForm setupForm, final BindingResult bindingResult) {
		if (setupInfo.getAdminPassword() != null) {
			final AppUserPrincipal admin = this.userService.createAdmin(setupForm, bindingResult);
			if (bindingResult.hasErrors())
				return "setup";
			final Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null,
					admin.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			setupInfo.setAdminPassword(null);
		}
		return "redirect:/";
	}
}
