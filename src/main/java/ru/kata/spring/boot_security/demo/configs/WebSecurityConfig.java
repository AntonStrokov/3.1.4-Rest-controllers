package ru.kata.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomUserDetailsService userDetailsService;
	private final SuccessUserHandler successUserHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// 1. Убираем лишние permitAll. Оставляем только логин.
				.authorizeRequests()
				.antMatchers("/", "/login", "/error").permitAll()
				// Все, что начинается на /admin — только для ADMIN
				.antMatchers("/admin/**").hasRole("ADMIN")
				// Страница /user — для обоих
				.antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
				// Любой другой запрос (включая /error и /) — только после логина!
				.anyRequest().authenticated()
				.and()
				// 2. Настройка формы логина
				.formLogin()
//				.loginPage("/login")
				.successHandler(successUserHandler)
				.permitAll()
				.and()
				// 3. Логаут
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
				.permitAll();
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

}