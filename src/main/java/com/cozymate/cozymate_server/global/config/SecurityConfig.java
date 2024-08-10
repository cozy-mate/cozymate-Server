package com.cozymate.cozymate_server.global.config;

import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.service.LogoutService;
import com.cozymate.cozymate_server.domain.auth.utils.JwtFilter;
import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    private final AuthService authService;

    private final LogoutService logoutService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(
                                        Arrays.asList("https://cozymate.store:3000",
                                                "https://cozymate.store"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }));

        //csrf disable
        httpSecurity
                .csrf((auth) -> auth.disable());

        //Form login 방식 disable
        httpSecurity
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        httpSecurity
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**", "/v2/swagger-config",
                                "/swagger-resources/**").permitAll()
                        .requestMatchers("/", "/oauth2/kakao/**").permitAll()
                        .requestMatchers("/", "/api/v3/check-nickname", "/api/v3/login", "/api/v3/join").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest()
                        .authenticated());

        //JWT 필터 추가
        httpSecurity
                .addFilterBefore(new JwtFilter(jwtUtil, authService),
                        UsernamePasswordAuthenticationFilter.class);

        //session 설정 (jwt 사용 -> stateless)
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그아웃 필터
        httpSecurity.logout((logout) -> logout
                .logoutUrl("/api/v3/logout") // 로그아웃 URL 설정
                .addLogoutHandler(logoutService) // LogoutHandler 등록
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().flush();
                })
        );

        return httpSecurity.build();

    }
}