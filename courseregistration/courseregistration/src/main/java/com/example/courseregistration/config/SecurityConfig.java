package com.example.courseregistration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/home", "/register", "/login", "/courses", "/courses/**").permitAll()
						.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
						.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/enroll/**").hasRole("STUDENT")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/home", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.defaultSuccessUrl("/home", true)
						.userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService)))
				.logout(logout -> logout
						.logoutSuccessUrl("/home")
						.permitAll());

		return http.build();
	}
}
