package com.colonelparrot.dbviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ltpeacock.dbviewer.commons.AppProperties;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, 
	scanBasePackages = {"com.ltpeacock.dbviewer", "com.colonelparrot.dbviewer"})
@EnableConfigurationProperties(AppProperties.class)
public class GrantsDbViewerApplication {
	private static final Logger LOG = LoggerFactory.getLogger(GrantsDbViewerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GrantsDbViewerApplication.class, args);
	}
}
