package com.cozymate.cozymate_server.domain.auth.controller;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.service.KakaoService;
import com.cozymate.cozymate_server.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/oauth2/kakao")
@Slf4j
@RequiredArgsConstructor
@RestController
public class KakaoController implements SocialLoginController {
    private final KakaoService kakaoService;
    private final AuthService authService;

    @Override
    @Operation(summary = "[말즈] 소셜로그인 url 요청 api",
            description = "get 요청 보내면 response body로 url 응답")
    @GetMapping(value = "/sign-in")
    public ResponseEntity<ApiResponse<UrlDTO>> signIn() {
        UrlDTO url = kakaoService.getRedirectUrl();

        log.info("redirect url: {}", url.getRedirectUrl());

        return ResponseEntity.ok(ApiResponse.onSuccess(url));
    }

    @Override
    @Operation(summary = "[말즈] 요청 x 카카오가 요청 보냄",
            description = "Header : accessToken or 임시 토큰, Body: requestToken or null")
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<AuthResponseDTO.TokenResponseDTO>> callBack(
            @RequestParam(required = false) String code) {
        // 인가코드를 기반으로 토큰(Access Token) 발급
        String token = kakaoService.getTokenByCode(code);

        // 토큰을 통해 사용자 정보 조회
        String clientId = kakaoService.getClientIdByToken(token);

        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = authService.generateTokenDTO(clientId);

        log.info("소셜로그인 사용자: {}", tokenResponseDTO.getMemberInfoDTO().getNickname());

        return ResponseEntity.ok(ApiResponse.onSuccess(tokenResponseDTO));

    }

}
