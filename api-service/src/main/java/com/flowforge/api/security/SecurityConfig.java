package com.flowforge.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    public SecurityConfig(ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/prometheus",
                                "/actuator/info"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(
                        apiKeyAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
