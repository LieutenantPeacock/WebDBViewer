package com.colonelparrot.dbviewer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
		http.authorizeRequests().antMatchers("/setup").permitAll()
				.anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll().and()
				.rememberMe().rememberMeParameter("remember-me").and().logout().logoutUrl("/logout")
				.logoutSuccessUrl("/");
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
}
