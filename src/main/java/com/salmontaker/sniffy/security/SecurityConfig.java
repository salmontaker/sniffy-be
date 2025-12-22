package com.salmontaker.sniffy.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173", "https://sniffy.64bit.kr"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private HttpSecurity applyCommonSettings(HttpSecurity http) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfig()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler));
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        applyCommonSettings(http);

        return http.securityMatchers(matchers -> matchers
                        .requestMatchers(HttpMethod.POST, "/api/auth/login")
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout")
                        .requestMatchers(HttpMethod.POST, "/api/users")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id:\\d+}")
                        .requestMatchers(HttpMethod.GET, "/api/agencies", "/api/agencies/{id:\\d+}")
                        .requestMatchers(HttpMethod.GET, "/api/found-items", "/api/found-items/{id:\\d+}", "/api/found-items/samples")
                        .requestMatchers(HttpMethod.GET, "/api/stats/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain protectedChain(HttpSecurity http) throws Exception {
        applyCommonSettings(http);

        return http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/agencies/**").authenticated()
                        .requestMatchers("/api/keywords/**").authenticated()
                        .requestMatchers("/api/notices/**").authenticated()
                        .requestMatchers("/api/push-subscriptions/**").authenticated()
                        .requestMatchers("/api/admin/**").access((authentication, context) -> {
                            String ip = context.getRequest().getRemoteAddr();
                            boolean allowed = ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1");

                            return new AuthorizationDecision(allowed);
                        })
                        .anyRequest().denyAll())
                .build();
    }
}
