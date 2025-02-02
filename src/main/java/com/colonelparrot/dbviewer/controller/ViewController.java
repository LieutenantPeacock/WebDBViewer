package com.colonelparrot.dbviewer.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.colonelparrot.dbviewer.db.TableData;
import com.ltpeacock.dbviewer.commons.AppUserPrincipal;
import com.ltpeacock.dbviewer.commons.DBConfig;
import com.ltpeacock.dbviewer.commons.RoleNames;
import com.ltpeacock.dbviewer.db.DBConstants;
import com.ltpeacock.dbviewer.db.DriverVO;
import com.ltpeacock.dbviewer.db.QueryResult;
import com.ltpeacock.dbviewer.db.SortDirection;
import com.ltpeacock.dbviewer.db.dto.AppUserDTO;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.db.dto.NamedQueryDTO;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.form.NewUserForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
import com.ltpeacock.dbviewer.response.MappedMultiErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;
import com.ltpeacock.dbviewer.service.DBConnectionService;
import com.ltpeacock.dbviewer.service.DriversService;
import com.ltpeacock.dbviewer.service.NamedQueryService;
import com.ltpeacock.dbviewer.service.UserService;
import com.ltpeacock.dbviewer.util.BuildInfo;
import com.ltpeacock.taglib.pagination.PageLinkGenerator;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Controller
@PreAuthorize("isAuthenticated()")
public class ViewController {
	private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private DriversService driversService;
	@Autowired
	private DBConnectionService dbConnectionService;
	@Autowired
	private PageLinkGenerator pageLinkGenerator;
	@Autowired
	private DBConfig dbConfig;
	@Autowired
	private NamedQueryService namedQueryService;
	@Autowired
	private BuildInfo buildInfo;

	@GetMapping("/")
	public String viewer(final Model model, @AuthenticationPrincipal final AppUserPrincipal principal,
			final @RequestParam(required = false) String connection,
			final @RequestParam(required = false) String table,
			final @RequestParam(defaultValue = "1") Integer page,
			final @RequestParam(required = false) String sort,
			final @RequestParam(required = false) SortDirection dir,
			final @RequestParam(required = false) String where,
			final HttpServletRequest request) {
		try {
			if(connection != null) {
				final long connectionId = Long.parseLong(connection);
				model.addAttribute("tables", dbConnectionService.getTables(connectionId, principal.getId()));
				if (table != null) {
					final SimpleResponse<TableData> res = dbConnectionService.getTableContents(connectionId, principal.getId(), table, page, sort, dir, where);
					if (res.getErrorMessage() == null) {
						final TableData tableData = res.getValue();
						model.addAttribute("tableContents", tableData);
						model.addAttribute("currentPage", page);
						model.addAttribute("lastPage", 
							(tableData.getTotalRows() + DBConstants.PAGE_SIZE - 1) / DBConstants.PAGE_SIZE);
						model.addAttribute("pageLinkGenerator", pageLinkGenerator);
						model.addAttribute("currentUri",
								ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
					} else {
						model.addAttribute("tableContentsError", res.getErrorMessage());
					}
				}
			} else if (table != null) {
				return "redirect:/";
			}
		} catch(NumberFormatException e) {
			return "redirect:/";
		}
		model.addAttribute("databases", dbConfig.getDatabases());
		model.addAttribute("connections", userService.getConnections(principal.getId()));
		model.addAttribute("drivers", driversService.getDriverPaths());
		model.addAttribute("queries", userService.getQueries(principal.getId()));
		model.addAttribute("version", buildInfo.get(BuildInfo.GIT_BUILD_VERSION));
		model.addAttribute("commitId", buildInfo.get(BuildInfo.GIT_COMMIT_ID_ABBREV));
		if (request.isUserInRole(RoleNames.ADMIN)) {
			model.addAttribute("users", userService.getAllUsers());
		}
		return "viewer";
	}
	
	@PostMapping("/uploadDriver")
	@Secured(RoleNames.ADMIN)
	@ResponseBody
	public MappedErrorsResponse<String> uploadDriver(final MultipartFile[] files, @RequestParam final String folder) {
		LOG.info("Uploaded {} files", files.length);
		return this.driversService.uploadDriver(files, folder);
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Map<String, String>> handleFileTooLarge(final MaxUploadSizeExceededException e) {
		LOG.error("MaxUploadSizeExceededException", e);
		final Throwable original = Optional.ofNullable(e).map(Throwable::getCause)
											.map(Throwable::getCause).orElse(null);
		if (original instanceof SizeLimitExceededException) {
			final SizeLimitExceededException s = (SizeLimitExceededException) original;
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
					.body(createSingleErrorMap(
						"Total request size (" + s.getActualSize() + " bytes) exceeds the maximum of " 
						+ s.getPermittedSize() + " bytes."));
		} else if (original instanceof FileSizeLimitExceededException) {
			final FileSizeLimitExceededException f = (FileSizeLimitExceededException) original;
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
					.body(createSingleErrorMap(
						"Maximum size for a single uploaded file (" + f.getPermittedSize() + " bytes) exceeded for file ["
						+ f.getFileName() + "]"));
		}
		return null;
	}
	
	private static Map<String, String> createSingleErrorMap(final String errorMessage){
		return Collections.singletonMap("errorMessage", errorMessage);
	}
	
	@GetMapping("/getDrivers")
	@ResponseBody
	public List<DriverVO> getDrivers(@RequestParam final String driverPath) throws IOException {
		return this.driversService.getDrivers(new File(driverPath));
	}
	
	@PostMapping("/newConnection")
	@Secured(RoleNames.ADMIN)
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
	
	@PostMapping("/newUser")
	@ResponseBody
	public MappedMultiErrorsResponse<AppUserDTO> createUser(final @Valid NewUserForm form, final BindingResult result) {
		return this.userService.createUser(form, result);
	}
	
	@GetMapping("/connectionDetails")
	@Secured(RoleNames.ADMIN)
	@ResponseBody
	public DBConnectionDefDTO getConnectionDetails(@RequestParam final long id) {
		return this.dbConnectionService.getConnectionDetails(id);
	}
	
	@GetMapping("/autocompleteUsersNotInConnection")
	@Secured(RoleNames.ADMIN)
	@ResponseBody
	public List<AppUserDTO> autocompleteUsers(@RequestParam final String text, @RequestParam final long connectionId){
		return this.userService.findUsersNotInConnectionWithSimilarUsername(connectionId, text);
	}
	
	@GetMapping("/userinfo")
	@Secured(RoleNames.ADMIN)
	@ResponseBody
	public SimpleResponse<AppUserDTO> userInfo(@RequestParam final String username) {
		return this.userService.getUserInfo(username);
	}
	
	@PostMapping("/updateConnection")
	@Secured(RoleNames.ADMIN)
	@ResponseBody
	public MappedMultiErrorsResponse<DBConnectionDefDTO> updateConnection(final @Valid ConnectionForm form, final BindingResult result) {
		return this.dbConnectionService.updateConnection(form, result);
	}
	
	@PostMapping("/testConnection")
	@ResponseBody
	public SimpleResponse<String> testConnection(final ConnectionForm form, 
			@AuthenticationPrincipal final AppUserPrincipal principal) {
		return this.dbConnectionService.testConnection(form, principal.getId());
	}
	
	@PostMapping("/newQuery")
	@ResponseBody
	public SimpleResponse<NamedQueryDTO> newQuery(final @RequestParam String name, final @RequestParam String sql,
			final @AuthenticationPrincipal AppUserPrincipal principal) {
		return this.namedQueryService.createNamedQuery(principal.getId(), name, sql);
	}
}
