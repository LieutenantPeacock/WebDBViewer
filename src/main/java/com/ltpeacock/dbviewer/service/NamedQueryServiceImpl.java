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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ltpeacock.dbviewer.db.dto.NamedQueryDTO;
import com.ltpeacock.dbviewer.db.entity.AppUser;
import com.ltpeacock.dbviewer.db.entity.NamedQuery;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;
import com.ltpeacock.dbviewer.db.repository.NamedQueryRepository;
import com.ltpeacock.dbviewer.response.SimpleResponse;

/**
 * @author LieutenantPeacock
 */
@Transactional
@Service
public class NamedQueryServiceImpl implements NamedQueryService {
	private static final Logger LOG = LoggerFactory.getLogger(NamedQueryServiceImpl.class);
	@Autowired
	private AppUserRepository userRepository;
	@Autowired
	private NamedQueryRepository queryRepository;
	
	@Override
	public SimpleResponse<NamedQueryDTO> createNamedQuery(final long userId, final String name, final String sql) {
		if (StringUtils.isBlank(name))
			return SimpleResponse.withErrorMessage("Name must not be blank.");
		final AppUser user = this.userRepository.findById(userId);
		final NamedQuery query = new NamedQuery();
		query.setName(name);
		query.setSql(sql);
		query.setUser(user);
		return SimpleResponse.withValue(new NamedQueryDTO(queryRepository.save(query)));
	}
}
