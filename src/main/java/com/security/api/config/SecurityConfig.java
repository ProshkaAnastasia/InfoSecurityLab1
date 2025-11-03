package com.security.api.config;
import com.security.api.filter.JwtAuthFilter;
import com.security.api.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @EnableWebSecurity
public class SecurityConfig {
  private final JwtAuthFilter jwtFilter; private final UserDetailsServiceImpl uds;
  public SecurityConfig(JwtAuthFilter f, UserDetailsServiceImpl u){this.jwtFilter=f;this.uds=u;}
  @Bean public SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable())
        .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth->auth.requestMatchers("/auth/**","/h2-console/**").permitAll().anyRequest().authenticated())
        .headers(h->h.frameOptions(f->f.sameOrigin()).contentSecurityPolicy(csp->csp.policyDirectives("default-src 'self'")))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();}
  @Bean public PasswordEncoder encoder(){return new BCryptPasswordEncoder(12);}  
  @Bean public AuthenticationManager authManager(AuthenticationConfiguration c) throws Exception{return c.getAuthenticationManager();}}