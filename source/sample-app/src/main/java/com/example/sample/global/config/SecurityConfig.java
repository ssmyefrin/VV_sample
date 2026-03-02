package com.example.sample.global.config;

import com.example.sample.global.security.CustomAuthenticationEntryPoint;
import com.example.sample.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomAuthenticationEntryPoint entryPoint;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        // 인증 없이 접근 가능한 URL 리스트 (화이트리스트)
        private static final String[] AUTH_WHITELIST = {
                        "/api/v1/users/signup",
                        "/api/v1/users/login",
                        "/api/v1/users/check/**",
                        "/v3/api-docs/**",
                        "/api/v1/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/h2-console/**",
                        "/favicon.ico",
                        "/error"
        };

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                // H2 Console을 위한 Header 설정 (이게 없으면 H2 화면이 깨짐)
                                .headers(headers -> headers.frameOptions(options -> options.disable()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(AUTH_WHITELIST).permitAll() // 화이트리스트는 모두 통과
                                                .anyRequest().authenticated() // 그 외는 모두 인증 필요
                                )
                                .exceptionHandling(handler -> handler
                                                .authenticationEntryPoint(entryPoint))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
