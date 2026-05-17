package com.colonelparrot.dbviewer;

import static com.colonelparrot.dbviewer.GrantsDbViewerApplication.COLONEL_PARROT_DBVIEWER_BASE;
import static com.colonelparrot.dbviewer.GrantsDbViewerApplication.LT_PEACOCK_DBVIEWER_BASE;

import java.io.File;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ltpeacock.dbviewer.commons.AppProperties;
import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@SpringBootApplication(scanBasePackages = {COLONEL_PARROT_DBVIEWER_BASE, LT_PEACOCK_DBVIEWER_BASE})
@EnableJpaRepositories(LT_PEACOCK_DBVIEWER_BASE)
@EntityScan(LT_PEACOCK_DBVIEWER_BASE)
@EnableConfigurationProperties(AppProperties.class)
public class GrantsDbViewerApplication {
	static final String COLONEL_PARROT_DBVIEWER_BASE = "com.colonelparrot.dbviewer";
	static final String LT_PEACOCK_DBVIEWER_BASE = "com.ltpeacock.dbviewer";
	private static final Logger LOG = LoggerFactory.getLogger(GrantsDbViewerApplication.class);
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	@Autowired
	private AppUserRepository userRepository;
	@Autowired
	private SetupInfo setupInfo;
	@Autowired
	private AppProperties appProps;

	public static void main(String[] args) {
		SpringApplication.run(GrantsDbViewerApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	private void setup() {
		final File dbViewerBase = new File(appProps.getAppRoot());
		LOG.info("DB Viewer base folder located at [{}]", dbViewerBase.getAbsolutePath());
		if (!dbViewerBase.exists()) {
			LOG.info("[STARTUP CONFIGURATION] No existing base folder found; automatically creating one.");
			if (!dbViewerBase.mkdirs())
				LOG.error("Could not create DB Viewer base folder!");
		}
		final String tempPassword = appProps.getTempPassword();
		if (userRepository.count() == 0) {
			LOG.info("New installation detected: there are currently no users.");
			final String password = tempPassword == null ? generateRandomString(16, false) : tempPassword;
			LOG.info("<<< {} [{}] >>>",
					(tempPassword == null ? "Generated temporary admin password": 
							"Using user provided temporary password"),
					password);
			setupInfo.setAdminPassword(password);
		} else if (tempPassword != null) {
			LOG.warn("This is not a new installation; user provided temporary password is ignored.");
		}
	}

	private static String generateRandomString(final int length, final boolean latinCharacters) {
		final int lowerBound = latinCharacters ? 97 : 34;
		final int upperBound = latinCharacters ? 122 : 125;
		return SECURE_RANDOM.ints(length, lowerBound, upperBound + 1)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}
}
