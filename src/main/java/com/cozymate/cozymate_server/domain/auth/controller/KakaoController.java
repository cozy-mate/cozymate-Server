package com.cozymate.cozymate_server.domain.auth.controller;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.service.KakaoService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@RequestMapping("/oauth2/kakao")
@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoController implements SocialLoginController {

    private static final String HTTP_ERROR_MESSAGE_FORMAT = "HTTP Error: %s, Status Code: %s, Response Body: %s";
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;

    private final KakaoService kakaoService;
    private final AuthService authService;

    @Override
    @Operation(summary = "[말즈] 소셜로그인 url 요청 api",
            description = "get 요청 보내면 response body로 url 응답")
    @GetMapping(value = "/url")
    public ResponseEntity<ApiResponse<UrlDTO>> getUrl() {
        UrlDTO url = kakaoService.getRedirectUrl();

        log.info(url.getRedirectUrl());

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.onSuccess(url));
    }

    @Override
    @Operation(summary = "[말즈] 요청 x 카카오가 요청 보냄",
            description = "Header : accessToken or 임시 토큰, Body: requestToken or null")
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<AuthResponseDTO.SocialLoginDTO>> callBack(
            @RequestParam(required = false) String code) {

        HttpEntity<MultiValueMap<String, String>> tokenRequest = kakaoService.makeTokenRequest(code);

        RestTemplate tokenRt = new RestTemplate();

        ResponseEntity<String> tokenResponse;
        try {
            tokenResponse = tokenRt.exchange(TOKEN_URI, HttpMethod.POST,
                    tokenRequest, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = String.format(HTTP_ERROR_MESSAGE_FORMAT,
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(errorMessage);
        }

        // 인가코드를 기반으로 토큰(Access Token) 발급
        String accessToken = kakaoService.parseAccessToken(tokenResponse);

        // 토큰을 통해 사용자 정보 조회
        HttpEntity<MultiValueMap<String, String>> clientInfoRequest = kakaoService.makeMemberInfoRequest(accessToken);

        RestTemplate clientInfoRt = new RestTemplate();
        ResponseEntity<String> clientInfoResponse;

        try {
            clientInfoResponse = clientInfoRt.exchange(USER_INFO_URI, HttpMethod.POST,
                    clientInfoRequest,
                    String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = String.format(HTTP_ERROR_MESSAGE_FORMAT,
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(errorMessage);
        }

        String clientId = kakaoService.getClientId(clientInfoResponse);

        String token = authService.generateToken(clientId);

        HttpHeaders headers = authService.addTokenAtHeader(token);

        AuthResponseDTO.SocialLoginDTO socialLoginDTO = authService.socialLogin(clientId);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.onSuccess(socialLoginDTO));

    }

}
