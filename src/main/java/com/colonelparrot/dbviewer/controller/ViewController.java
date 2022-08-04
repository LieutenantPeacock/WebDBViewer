package com.colonelparrot.dbviewer.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.db.DriverVO;
import com.ltpeacock.dbviewer.db.QueryResult;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;
import com.ltpeacock.dbviewer.service.DBConnectionService;
import com.ltpeacock.dbviewer.service.DriversService;
import com.ltpeacock.dbviewer.service.UserService;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
public class ViewController {
	private static final String INVALID_FIELD_VALUE = "Invalid field value";
	private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private DriversService driversService;
	@Autowired
	private DBConnectionService dbConnectionService;

	@GetMapping("/")
	public String getStuff(final Model model, @AuthenticationPrincipal final AppUserPrincipal principal,
			final @RequestParam(required = false) String connection,
			final @RequestParam(required = false) String table) {
		try {
			if(connection != null) {
				final long connectionId = Long.parseLong(connection);
				model.addAttribute("tables", dbConnectionService.getTables(connectionId, principal.getId()));
				if (table != null)
					model.addAttribute("tableContents", dbConnectionService.getTableContents(connectionId, principal.getId(), table));
			} else if (table != null) {
				return "redirect:/";
			}
		} catch(NumberFormatException e) {
			return "redirect:/";
		}
		model.addAttribute("connections", userService.getConnections(principal.getId()));
		model.addAttribute("drivers", driversService.getDriverPaths());
		return "viewer";
	}
	
	@PostMapping("/uploadDriver")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public MappedErrorsResponse<String> uploadDriver(final MultipartFile[] files, @RequestParam final String folder) {
		LOG.info("Uploaded {} files", files.length);
		return this.driversService.uploadDriver(files, folder);
	}
	
	@GetMapping("/getDrivers")
	@ResponseBody
	public List<DriverVO> getDrivers(@RequestParam final String driverPath) throws IOException {
		return this.driversService.getDrivers(new File(driverPath));
	}
	
	@PostMapping("/newConnection")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public MappedErrorsResponse<DBConnectionDefDTO> newConnection(@Valid final ConnectionForm form, final BindingResult result, @AuthenticationPrincipal final AppUserPrincipal principal) {
		return this.dbConnectionService.createConnection(form, result, principal.getId());
	}
	
	@PostMapping("/execute")
	@ResponseBody
	public SimpleResponse<QueryResult> execute(final @RequestParam String statement,
			final @RequestParam long connection,
			@AuthenticationPrincipal final AppUserPrincipal principal) {
		return this.dbConnectionService.executeQuery(connection, principal.getId(), statement);
	}
}
