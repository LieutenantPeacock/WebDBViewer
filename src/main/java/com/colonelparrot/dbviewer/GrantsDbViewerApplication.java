package com.colonelparrot.dbviewer;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ltpeacock.dbviewer.commons.ANSIEscapes;
import com.ltpeacock.dbviewer.commons.AppProperties;
import com.ltpeacock.dbviewer.commons.SetupInfo;
import com.ltpeacock.dbviewer.db.repository.AppUserRepository;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@SpringBootApplication(scanBasePackages = {"com.ltpeacock.dbviewer", "com.colonelparrot.dbviewer"})
@EnableConfigurationProperties(AppProperties.class)
public class GrantsDbViewerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(GrantsDbViewerApplication.class);
	@Autowired
	private AppUserRepository userRepository;
	@Autowired
	private SetupInfo setupInfo;
	@Autowired
	private AppProperties appProps;

	public static void main(String[] args) {
		SpringApplication.run(GrantsDbViewerApplication.class, args);
	}

	@PostConstruct
	public void setup() {
		final File dbViewerBase = new File(appProps.getAppRoot());
		LOG.info("DB Viewer base folder located at [{}]", dbViewerBase.getAbsolutePath());
		if (!dbViewerBase.exists()) {
			LOG.info(ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
					+ " No existing base folder found; automatically creating one.");
			if (!dbViewerBase.mkdirs())
				LOG.error("Could not create DB Viewer base folder!");
		}
		final String tempPassword = appProps.getTempPassword();
		if (userRepository.count() == 0) {
			LOG.info("New installation detected: there are currently no users.");
			final String password = tempPassword == null ? generateRandomString(8, false) : tempPassword;
			LOG.info("{} " + ANSIEscapes.YELLOW + "[{}]" + ANSIEscapes.RESET,
					(tempPassword == null ? "Generated temporary admin password": 
							"Using user provided temporary password"),
					password);
			setupInfo.setAdminPassword(password);
		} else if(tempPassword != null) {
			LOG.warn("This is not a new installation; user provided temporary password is ignored.");
		}
	}

	private static String generateRandomString(final int length, final boolean latinCharacters) {
		int lowerBound = 34;
		int upperBound = 125;
		if (latinCharacters) {
			lowerBound = 97;
			upperBound = 122;
		}
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			str.append((char) randInt(lowerBound, upperBound));
		}
		return str.toString();
	}

	private static int randInt(final int min, final int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
