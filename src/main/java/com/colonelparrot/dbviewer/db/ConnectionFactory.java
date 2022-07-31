package com.colonelparrot.dbviewer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ColonelParrot
 * @version 1.1
 */
public class ConnectionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);

	public static Connection getConnection() {
		return getConnection("");
	}

	public static Connection getConnection(final String databaseName) {
		Connection con = null;
		try {
			String url = "jdbc:mariadb://localhost/" + databaseName;
			String username = "fullviewer";
			String password = "";
			LOG.info("Starting connection to [{}] with username [{}] and password [{}]", url, username,
					password.replaceAll(".", "*"));
			con = DriverManager.getConnection(url, username, password);
			LOG.info("Successfully connected to [{}]", url);
		} catch (SQLException e) {
			LOG.error("Error occured: [{}] ", e);
		}
		return con;
	}

}
