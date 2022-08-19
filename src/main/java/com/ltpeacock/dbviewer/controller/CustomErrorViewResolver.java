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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ltpeacock.dbviewer.commons.AppProperties;
import com.ltpeacock.dbviewer.commons.LogMarkers;
import com.ltpeacock.dbviewer.commons.RoleNames;

/**
 * @author LieutenantPeacock
 */
@Component
public class CustomErrorViewResolver implements ErrorViewResolver {
	private static final Logger LOG = LoggerFactory.getLogger(CustomErrorViewResolver.class);
	@Autowired
	private AppProperties appProps;

	@Override
	public ModelAndView resolveErrorView(final HttpServletRequest request, final HttpStatus status,
			final Map<String, Object> model) {
		LOG.info("Error status [{}]", status);
		final Map<String, Object> newModel = new HashMap<>(model);
		final String view;
		if (status == HttpStatus.NOT_FOUND) {
			view = "404";
		} else if (status == HttpStatus.FORBIDDEN) {
			LOG.warn(LogMarkers.FORBIDDEN_ACCESS, "Forbidden access to [{}] from [{}]",
					ServletUriComponentsBuilder.fromRequest(request).build().toString(), 
					request.getRemoteAddr());
			view = "access_denied";
		} else {
			newModel.put("showRawError", request.isUserInRole(RoleNames.ADMIN) || appProps.isShowRawExceptionsToUsers());
			view = "error";
		}
		return new ModelAndView(view, newModel);
	}
}
