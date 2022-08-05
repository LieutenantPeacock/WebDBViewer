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
package com.ltpeacock.dbviewer.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ltpeacock.dbviewer.db.entity.AppUser;
import com.ltpeacock.dbviewer.db.entity.DBConnectionDef;

/**
 * @author LieutenantPeacock
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long>{
	AppUser findByUsernameIgnoreCase(String username);
	AppUser findById(long id);
	@Query("select distinct u from AppUser u where u.username like CONCAT('%',:username,'%') and :connection not member of u.connections")
	List<AppUser> findUsersNotInConnectionWithSimilarUsername(@Param("connection") final DBConnectionDef connection, 
				@Param("username") final String username);
}
