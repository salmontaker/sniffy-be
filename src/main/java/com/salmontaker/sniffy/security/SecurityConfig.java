package com.salmontaker.sniffy.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
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
        configuration.setAllowedOriginPatterns(List.of("*"));
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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler));
    }

    @Bean
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        applyCommonSettings(http);
        return http.securityMatcher("/api/user", "/api/auth/login", "/api/agency", "/api/found-items", "/api/stats/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/api/user/\\d+")).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agency").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agency/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/found-items").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stats/**").permitAll()
                        .anyRequest().denyAll()
                )
                .build();
    }

    @Bean
    public SecurityFilterChain protectedChain(HttpSecurity http) throws Exception {
        applyCommonSettings(http);
        return http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/auth/**").authenticated()
                        .requestMatchers("/api/agency/**").authenticated()
                        .requestMatchers("/api/found-items/**").authenticated()
                        .requestMatchers("/api/keyword/**").authenticated()
                        .requestMatchers("/api/notice/**").authenticated()
                        .requestMatchers("/api/push-subscription/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter((jwtToken -> {
                            String sub = jwtToken.getSubject();
                            Integer userId = Integer.valueOf(sub);

                            return new UsernamePasswordAuthenticationToken(
                                    userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                        })))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .build();
    }
}
