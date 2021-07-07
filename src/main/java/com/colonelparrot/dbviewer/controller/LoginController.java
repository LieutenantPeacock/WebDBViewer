package com.colonelparrot.dbviewer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.colonelparrot.dbviewer.commons.HashEngine;
import com.colonelparrot.dbviewer.config.LoginResponse;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
public class LoginController {
	private static final String DB_VIEWER_CONFIG_AUTH_PROPERTIES = "/db-viewer-config/auth.properties";
	private static final String PROP_PASSWORD_KEY = "password";
	private static final String PROP_USERNAME_KEY = "username";
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String redirectToViewer(Model model) {
		return "redirect:/viewer";
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public LoginResponse authenticate(@RequestParam(value = PROP_USERNAME_KEY, required = false) String username,
			@RequestParam(value = PROP_PASSWORD_KEY, required = false) String password, HttpSession session,
			HttpServletRequest request) {
		final String IP = request.getRemoteAddr();
		LOG.info("Authentication request from IP [{}]", IP);
		LoginResponse response = new LoginResponse();
		if(!isEmptyParam(username, password)) {
			if (authenticate(username, password)) {
				LOG.info("Successful authentication request from IP [{}]", IP);
				response.setSuccess(true);
				session.setAttribute("isLoggedIn", "true");
			} else {
				LOG.info("Failed authentication request from IP [{}] with username [{}] and password [{}]",
						IP, username, password);
				response.setSuccess(false);
				response.setMessage("Incorrect username and/or password");
			}
		}else {
			response.setSuccess(false);
			response.setMessage("Please fill all required fields");
		}
		return response;
	}

	private boolean authenticate(String username, String password) {
		File authenticationProperties = new File(DB_VIEWER_CONFIG_AUTH_PROPERTIES);
		if (authenticationProperties.exists()) {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(authenticationProperties));
				String correctUsername = prop.getProperty(PROP_USERNAME_KEY);
				String correctPassword = prop.getProperty(PROP_PASSWORD_KEY);
				if (correctUsername.equals(username)) {
					LOG.info("Authentication - Username matches");
					HashEngine hashEngine = new HashEngine();
					final String hashedPassword = hashEngine.SHA256(password);
					if (correctPassword.equals(hashedPassword)) {
						LOG.info("Authentication - Password matches, successful");
						return true;
					} else {
						LOG.info("Authentication - Password does not match. Hashed [{}]", hashedPassword);
						return false;
					}	
				} else {
					LOG.info("Authentication - Username does not match");
					return false;
				}
			} catch (Exception e) {
				LOG.error("Error occured [{}]", e);
				return false;
			}
		} else {
			LOG.info("No authentication properties file found at [{}], failing auth request",
					authenticationProperties.getAbsolutePath());
			return false;
		}
	}
	public static boolean isEmptyParam(String... params) {
		for (String param : params) {
			if (null == param || param.length() < 1) {
				return true;
			}
		}
		return false;
	}

}
