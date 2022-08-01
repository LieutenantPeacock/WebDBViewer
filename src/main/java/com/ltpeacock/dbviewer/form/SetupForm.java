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
package com.ltpeacock.dbviewer.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author LieutenantPeacock
 */
public class SetupForm {
	@NotBlank(message = "This field is required.")
	private String tempPassword;
	@NotBlank(message = "Username must not be blank.")
	@Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters (inclusive).")
	private String username;
	@NotBlank(message = "Password must not be blank.")
	@Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters (inclusive).")
	private String password;
	private String confirmPassword;

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
