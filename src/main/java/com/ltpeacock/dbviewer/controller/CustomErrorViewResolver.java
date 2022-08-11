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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author LieutenantPeacock
 */
@Component
public class CustomErrorViewResolver implements ErrorViewResolver {
	private static final Logger LOG = LoggerFactory.getLogger(CustomErrorViewResolver.class);
	@Override
	public ModelAndView resolveErrorView(final HttpServletRequest request, final HttpStatus status, 
			final Map<String, Object> model) {
		LOG.info("Error status [{}]", status);
		if (status == HttpStatus.NOT_FOUND) {
			return new ModelAndView("404");
		}
		if (status == HttpStatus.FORBIDDEN)
			return new ModelAndView("access_denied");
		return new ModelAndView("error");
	}
}
