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
package com.ltpeacock.dbviewer.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author LieutenantPeacock
 */
public class Util {
	private Util() {
		throw new UnsupportedOperationException();
	}

	private static final Pattern CARRIAGE_RETURN_PAT = Pattern.compile("\r");
	private static final Pattern NEWLINE_PAT = Pattern.compile("\n");

	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated();
	}

	public static Map<String, String> toErrorMap(final BindingResult result) {
		return result.getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
	}
	
	public static Map<String, List<String>> toMultiErrorMap(final BindingResult result) {
		return result.getFieldErrors().stream()
				.collect(Collectors.groupingBy(FieldError::getField, 
						Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
	}

	public static String replaceLineBreaks(final String str) {
		return str == null ?
				"null"
				: NEWLINE_PAT.matcher(
					CARRIAGE_RETURN_PAT.matcher(str).replaceAll("\\\\R")
				).replaceAll("\\\\N");
	}
	
	public static String toOneLine(final Throwable t) {
		return replaceLineBreaks(ExceptionUtils.getStackTrace(t));
	}
	
	public static String toOneLine(final Object o) {
		return replaceLineBreaks(String.valueOf(o));
	}
}
