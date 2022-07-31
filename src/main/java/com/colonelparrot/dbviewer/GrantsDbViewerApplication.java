package com.colonelparrot.dbviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@SpringBootApplication
public class GrantsDbViewerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(GrantsDbViewerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GrantsDbViewerApplication.class, args);
	}
}
