package com.colonelparrot.dbviewer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
public class LoginController {
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@GetMapping("/login")
	public String redirectToViewer() {
		return "login";
	}
}
