package com.ltpeacock.dbviewer.util;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a + "\n" + b));
	}

	public static String replaceLineBreaks(final String str) {
		return str == null ?
				"null"
				: NEWLINE_PAT.matcher(
					CARRIAGE_RETURN_PAT.matcher(str).replaceAll("\\R")
				).replaceAll("\\N");
	}
}
