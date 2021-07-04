package com.colonelparrot.dbviewer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.mariadb.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ColonelParrot
 * @version 1.0
 */
public class ConnectionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
	Connection con;

	public Connection getConnect() {
		return getConnect("");
	}

	public Connection getConnect(String databaseName) {
		try {
			Class.forName(Driver.class.getName());// "org.mariadb.jdbc.Driver"
		} catch (Exception e) {
			LOG.error("Error occured: [{}] ", e);
		}
		try {
			String url = "jdbc:mariadb://localhost/" + databaseName;
			String username = "fullviewer";
			String password = "";
			LOG.info("Starting connection to [{}] with username [{}] and password [{}]", url, username,
					password.replaceAll(".", "*"));
			con = DriverManager.getConnection(url, username, password);
			LOG.debug("Successfully connected to [{}]", url);
		} catch (SQLException e) {
			LOG.error("Error occured: [{}] ", e);
		}
		return con;
	}

}
