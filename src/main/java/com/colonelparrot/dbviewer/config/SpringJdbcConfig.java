package com.colonelparrot.dbviewer.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * @author Grant Ou
 * @version 1.1
 */
@Configuration
public class SpringJdbcConfig {
	@Bean
	public DataSource mysqlDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost/");
		dataSource.setUsername("OuMedia");
		dataSource.setPassword("grant");
		return dataSource;
	}
}