package com.cozymate.cozymate_server.auth.controller;

import com.cozymate.cozymate_server.auth.utils.JwtUtil;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    @Value("${oauth.google.client_id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${oauth.google.client_secret}")
    private String GOOGLE_CLIENT_SECRET;
    private static final String REDIRECT_URI = "https://api.cozymate.shop/admin/auth/callback";
    private static final String SCOPE = "email profile";
    private static final String RESPONSE_TYPE = "code";
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static final String ADMIN_EMAIL = "cozymate0@gmail.com";
    private static final String ADMIN_REDIRECT_URL = "https://cozymate-admin.web.app/inquiry";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String COOKIE_NAME = "JWT";
    private static final int COOKIE_MAX_AGE = -1;
    private static final String FORBIDDEN_MESSAGE = "관리자 계정이 아닙니다.";

    private final JwtUtil jwtUtil;


    @GetMapping("/login")
    public ResponseEntity<Void> getGoogleLoginUrl() {
        String state = UUID.randomUUID().toString();
        String redirectUrl = buildGoogleLoginUrl(state);

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, redirectUrl)
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) throws IOException {
        String accessToken = requestAccessToken(code);
        String email = fetchUserEmail(accessToken);

        if (!ADMIN_EMAIL.equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FORBIDDEN_MESSAGE);
        }

        return issueAdminJwtAndRedirect();
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAdminAccess() {
        return ResponseEntity.ok("관리자 전용 접근 성공!");
    }
    private String buildGoogleLoginUrl(String state) {
        return UriComponentsBuilder
            .fromHttpUrl(GOOGLE_AUTH_URL)
            .queryParam("client_id", GOOGLE_CLIENT_ID)
            .queryParam("redirect_uri", REDIRECT_URI)
            .queryParam("response_type", RESPONSE_TYPE)
            .queryParam("scope", SCOPE)
            .queryParam("access_type", "offline")
            .queryParam("state", state)
            .toUriString();
    }

    private String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("client_secret", GOOGLE_CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("grant_type", GRANT_TYPE);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(
            GOOGLE_TOKEN_URL, tokenRequest, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new GeneralException(ErrorStatus._ADMIN_GOOGLE_AUTHENTICATION_FAIL);
        }

        return Optional.ofNullable(response.getBody())
            .map(body -> (String) body.get("access_token"))
            .orElseThrow(() -> new IllegalStateException("구글의 `access_token`이 없음"));
    }

    private String fetchUserEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(
            GOOGLE_USERINFO_URL,
            HttpMethod.GET,
            request,
            Map.class
        );

        return Optional.ofNullable(response.getBody())
            .map(body -> (String) body.get("email"))
            .orElseThrow(() -> new IllegalStateException("구글의 `email`이 없음"));
    }



    private ResponseEntity<String> issueAdminJwtAndRedirect() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, jwtUtil.generateAdminToken())
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .maxAge(COOKIE_MAX_AGE)
            .build();

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.LOCATION, ADMIN_REDIRECT_URL)
            .build();
    }
}
