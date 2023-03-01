package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter{
	
	//We need the bean of our UserDetailsServiceImpl security setup
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}
	
	//We need the bean of BCryptPasswordEncoder for encoding our password
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//We need bean of DaoAuthenticationProvider because we will be our providing security for dao(memory/db) objects 
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
		return daoAuthenticationProvider;
	}

	//Configure method..
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.authenticationProvider());
	}

	//here we need to configure to which url need to protected
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")//for ADMIN role allow /admin/** pages 
		.antMatchers("/user/**").hasRole("USER") //for USER role allow /user/** pages
		.antMatchers("/**").permitAll()//for all allow /** page
		.and().formLogin()//allow formLogin
		.loginPage("/signin")//our own signin page
		.defaultSuccessUrl("/user/index")//if login successful
		.loginProcessingUrl("/dologin")
		//.failureUrl("/login-fail")
		.and().csrf().disable();//disable all csrf(Cross-site Request Forgery)
	}
	
}
