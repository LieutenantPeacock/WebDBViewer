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

import com.ltpeacock.dbviewer.db.dto.NamedQueryDTO;
import com.ltpeacock.dbviewer.response.SimpleResponse;

/**
 * @author LieutenantPeacock
 */
public interface NamedQueryService {
	SimpleResponse<NamedQueryDTO> createNamedQuery(final long userId, final String name, final String sql);
}
