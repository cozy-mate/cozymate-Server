package com.cozymate.cozymate_server.global.config;

import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.utils.LoginFilter;
import com.cozymate.cozymate_server.domain.auth.utils.jwt.JwtFilter;
import com.cozymate.cozymate_server.domain.auth.utils.jwt.JwtUtil;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
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
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    private final MemberRepository memberRepository;
    private final AuthService authService;

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

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
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
                .addFilterBefore(new JwtFilter(jwtUtil,authService), LoginFilter.class); // loginFilter 이전에 jwtFilter 추가

        //로그인 필터 추가
        httpSecurity
                .addFilterAt(
                        new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, tokenRepository,
                                memberRepository, new ObjectMapper()),
                        UsernamePasswordAuthenticationFilter.class);

        //session 설정 (jwt 사용 -> stateless)
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // todo : 로그아웃 필터 추가

        return httpSecurity.build();

    }
}