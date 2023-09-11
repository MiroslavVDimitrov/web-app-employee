package com.miro.employee.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.miro.employee.security.jwt.AuthEntryPointJwt;
import com.miro.employee.security.jwt.AuthTokenFilter;
import com.miro.employee.security.service.UserDetailsServiceImpl;

/* WebSecurityConfigurerAdapterе същината на внедряването на нашата сигурност. 
Осигурява HttpSecurityконфигурации за конфигуриране на cors, csrf, управление на сесии,
 правила за защитени ресурси. Можем също да разширим и персонализираме конфигурацията по подразбиране,
  която съдържа елементите по-долу. 
  
  – UsernamePasswordAuthenticationTokenполучава {потребителско име, парола} от заявка за влизане,
   AuthenticationManagerще го използва за удостоверяване на акаунт за влизане.

  – AuthenticationManagerима DaoAuthenticationProvider(с помощта на UserDetailsServiceи PasswordEncoder) 
  за валидиране UsernamePasswordAuthenticationTokenобект. При успех, AuthenticationManagerвръща напълно
   попълнен обект за удостоверяване (включително предоставени права).
  
  – OncePerRequestFilterправи едно изпълнение за всяка заявка към нашия API. Той осигурява a doFilterInternal()метод, 
  който ще приложим анализиране и валидиране на JWT, зареждане на потребителски данни (използвайки UserDetailsService),
   проверявайки Authorization (с помощта на UsernamePasswordAuthenticationToken).
  
  – AuthenticationEntryPointще улови неупълномощена грешка и ще върне 401, когато Клиентите имат достъп до 
  защитени ресурси без удостоверяване.
  
  Хранилището съдържа UserRepositoryи RoleRepositoryза работа с база данни, ще бъдат импортирани в Controller .
  Контролерът получава и обработва заявката, след като е била филтрирана от OncePerRequestFilter.  */

@Configuration
@EnableMethodSecurity
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.requestMatchers("/**").permitAll()
            .requestMatchers("/**").permitAll()
            .anyRequest().authenticated());

    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}