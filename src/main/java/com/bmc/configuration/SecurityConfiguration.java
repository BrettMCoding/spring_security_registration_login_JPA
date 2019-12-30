package com.bmc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bmc.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
        .passwordEncoder(getPasswordEncoder());
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// permit unauthenticated requests to all named URLs.
		// deny unauthenticated requests to **/secured/** URLS.
		// I have not found a way to blanket allow URLS and then lock secured ones. 
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/register").permitAll()
            .antMatchers("/confirm").permitAll()
            .antMatchers("/forgot").permitAll()
            .antMatchers("/forgotconfirm").permitAll()
            
			.antMatchers("**/secured/**").permitAll().anyRequest().authenticated()
				.and()
				.httpBasic();
	}
	
	// BCrypt passwords for server-side storage.
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

	
}