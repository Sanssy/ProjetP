package com.example.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // This is the primary spring security annotation that is used to enable web security in a project.
@EnableGlobalMethodSecurity( //This is used to enable method level security based on annotations.
		securedEnabled = true, // It enables the @Secured annotation using which you can protect your controller/service methods
		jsr250Enabled = true, // It enables the @RolesAllowed annotation that can be used
		prePostEnabled = true //  It enables more complex expression based access control syntax with @PreAuthorize and @PostAuthorize annotations
)
public class SecurityConfig extends WebSecurityConfigurerAdapter { // It provides default security configurations and allows other classes to extend it and customize the security configurations by overriding its methods.

	@Autowired
	CustomUserDetailsService customUserDetailService; // To authenticate a User or perform various role-based checks, Spring security needs to load users details somehow. 
	
	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler; // This class is used to return a 401 unauthorized error to clients that try to access a protected resource without proper authentication. 
	
	/**
	 * JWTAuthenticationFilter to implement a filter that:
	 * reads JWT authentication token from the Authorization header of all the requests
	 * validates the token
	 * loads the user details associated with that token
	 * Sets the user details in Spring Security’s SecurityContext - 
	 * Spring Security uses the user details to perform authorization checks
	 * We can also access the user details stored in the SecurityContext
	 * in our controllers to perform our business logic
	 * */
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	/**
	 * AuthenticationManagerBuilder is used to create an AuthenticationManager instance
	 * which is the main Spring Security interface for authenticating a user.
	 */
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder
			.userDetailsService(customUserDetailService)
			.passwordEncoder(passwordEncoder());
	}
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * The HttpSecurity configurations are used to configure security functionalities like csrf,
	 * sessionManagement, and add rules to protect resources based on various conditions.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
					.and()
				.csrf()
					.disable()
				.exceptionHandling()
					.authenticationEntryPoint(unauthorizedHandler)
					.and()
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
				.authorizeRequests()
					.antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                        .permitAll()
                    .antMatchers("/api/auth/**")
                        .permitAll()
                    .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated(); 
	
		// add our custom JWT Security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	
	
	}
	
	
}
