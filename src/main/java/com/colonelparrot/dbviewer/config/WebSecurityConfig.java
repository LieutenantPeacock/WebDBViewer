package com.colonelparrot.dbviewer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.ltpeacock.dbviewer.commons.ANSIEscapes;
import com.ltpeacock.dbviewer.commons.Constants;

/**
 * @author ColonelParrot
 * @version 1.1
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
	private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated()
				.and().formLogin().loginPage("/login").permitAll()
				.and().logout()
				.logoutUrl("/logout").logoutSuccessUrl("/");
		return http.build();
	}
	
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/resources/**");
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService users(final PasswordEncoder encoder) {
		final File configFolder = new File(Constants.CONFIG_FOLDER_PATH);
		LOG.info("Configuration folder located at [{}]", configFolder.getAbsolutePath());
		if (!configFolder.exists()) {
			LOG.info(ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
					+ " No configuration folder found, automatically creating one");
			configFolder.mkdirs();
		}
		final File authenticationProperties = new File(configFolder, "auth.properties");
		if (!authenticationProperties.exists()) {
			boolean created = false;
			try {
				created = authenticationProperties.createNewFile();
			} catch (Exception e) {
			}
			if (created)
				LOG.info(
						ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
								+ " No authentication properties file found, automatically creating one at [{}]",
						authenticationProperties.getAbsolutePath());
			else
				LOG.error(ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
						+ " Failed to create authentication properties file");
		}
		final Properties props = new Properties();
		try {
			try (FileInputStream fis = new FileInputStream(authenticationProperties)) {
				props.load(fis);
			}
			String username = props.getProperty(Constants.AUTH_USERNAME_KEY);
			boolean hasWritten = false;
			if (username == null) {
				hasWritten = true;
				username = generateRandomString(6, true);
				LOG.info(ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
						+ " No username property found. Automatically generated one: [" + ANSIEscapes.RED + "{}"
						+ ANSIEscapes.RESET + "]", username);
				props.setProperty(Constants.AUTH_USERNAME_KEY, username);
			}
			String password = props.getProperty(Constants.AUTH_PASSWORD_KEY);
			if (password == null) {
				hasWritten = true;
				password = generateRandomString(8, false);
				LOG.info(ANSIEscapes.YELLOW + "[STARTUP CONFIGURATION]" + ANSIEscapes.RESET
						+ " No password property found. Automatically generated one: [" + ANSIEscapes.RED + "{}"
						+ ANSIEscapes.RESET + "]", password);
				props.setProperty(Constants.AUTH_PASSWORD_KEY, encoder.encode(password));
			}
			if (hasWritten)
				try (FileOutputStream fos = new FileOutputStream(authenticationProperties)) {
					props.store(fos, null);
				}
		} catch (Exception e) {
			LOG.error("Exception", e);
		}
		final UserDetails user = User.withUsername(props.getProperty(Constants.AUTH_USERNAME_KEY))
				.password(props.getProperty(Constants.AUTH_PASSWORD_KEY)).roles("USER").build();
		return new InMemoryUserDetailsManager(user);
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
