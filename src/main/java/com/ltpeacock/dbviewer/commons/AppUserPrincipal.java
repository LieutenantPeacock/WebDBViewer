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
package com.ltpeacock.dbviewer.commons;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ltpeacock.dbviewer.db.entity.AppUser;

/**
 * @author LieutenantPeacock
 */
public class AppUserPrincipal implements UserDetails {
	private static final long serialVersionUID = 9093247020773668757L;
	private long id;
	private String username;
	private String password;
	private boolean enabled;
	private boolean locked;
	private Instant expiryTime;
	private List<GrantedAuthority> authorities;

	public AppUserPrincipal(final AppUser user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.enabled = user.isEnabled();
		this.locked = user.isLocked();
		this.expiryTime = user.getExpiryTime();
		this.authorities = user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.expiryTime == null || this.expiryTime.isAfter(Instant.now());
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isAccountNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public long getId() {
		return this.id;
	}
}
