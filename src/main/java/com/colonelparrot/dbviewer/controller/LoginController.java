package com.colonelparrot.dbviewer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.util.Util;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
public class LoginController {
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private SetupInfo setupInfo;

	@GetMapping("/login")
	public String login() {
		if (Util.isAuthenticated())
			return "redirect:/";
		return setupInfo.getAdminPassword() == null ? "login" : "redirect:/setup";
	}
}
