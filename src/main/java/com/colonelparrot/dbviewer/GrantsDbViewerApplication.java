package com.colonelparrot.dbviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.colonelparrot.dbviewer.commons.HashEngine;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@SpringBootApplication
public class GrantsDbViewerApplication {
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	private static final String DB_VIEWER_CONFIG_FOLDER = "/db-viewer-config/";
	private static final Logger LOG = LoggerFactory.getLogger(GrantsDbViewerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GrantsDbViewerApplication.class, args);
		startupConfiguration();
	}

	private static void startupConfiguration() {
		HashEngine hashEngine = new HashEngine();
		File configFolder = new File(DB_VIEWER_CONFIG_FOLDER);
		if (!configFolder.exists()) {
			LOG.info(ANSI_YELLOW+"[STARTUP CONFIGURATION]"+ANSI_RESET+" No configuration folder found, automatically creating one");
			configFolder.mkdirs();
		}
		File authenticationProperties = new File(configFolder, "auth.properties");
		if (!authenticationProperties.exists()) {
			try {
				authenticationProperties.createNewFile();
				LOG.info(ANSI_YELLOW+"[STARTUP CONFIGURATION]"+ANSI_RESET+" No authentication properties file found, automatically creating one at [{}]", authenticationProperties.getAbsolutePath());
			} catch (Exception e) {
				LOG.error(ANSI_YELLOW+"[STARTUP CONFIGURATION]"+ANSI_RESET+" Failed to create authentication properties file");
			}
		}
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(authenticationProperties));
			String username = prop.getProperty("username");
			Random rand = new Random();
			boolean hasWritten = false;
			if (username == null) {
				hasWritten = true;
				String randomUsername = generateRandomString(6, rand, true);
				LOG.info(ANSI_YELLOW+"[STARTUP CONFIGURATION]"+ANSI_RESET+" No username property found. Automatically generated one: ["+ANSI_RED+"{}"+ANSI_RESET+"]", randomUsername);
				prop.setProperty("username", randomUsername);
			}
			String password = prop.getProperty("password");
			if(password == null) {
				hasWritten = true;
				String randomPassword = generateRandomString(8, rand, false);
				LOG.info(ANSI_YELLOW+"[STARTUP CONFIGURATION]"+ANSI_RESET+" No password property found. Automatically generated one: ["+ANSI_RED+"{}"+ANSI_RESET+"]", randomPassword);
				prop.setProperty("password", hashEngine.SHA256(randomPassword));
			}
			if(hasWritten) {
				prop.store(new FileOutputStream(authenticationProperties), "Password is hashed (SHA-256)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String generateRandomString(int length, Random rand, boolean latinCharacters) {
		int lowerBound = 34;
		int upperBound = 125;
		if(latinCharacters) {
			lowerBound = 97;
			upperBound = 122;
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			str.append((char) randInt(lowerBound, upperBound, rand));
		}
		return str.toString();
	}

	public static int randInt(int min, int max, Random rand) {

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}
}
