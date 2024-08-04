package com.cozymate.cozymate_server.domain.auth.controller;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.service.KakaoService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

    @GetMapping(value = "/url")
    public ResponseEntity<ApiResponse<UrlDTO>> getUrl() {
        UrlDTO url = kakaoService.getRedirectUrl();

        log.info(url.getRedirectUrl());

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.onSuccess(url));
    }

    @Override
    @GetMapping("/code")
    public ResponseEntity<ApiResponse<AuthResponseDTO.SocialLoginDTO>> callBack(
            @RequestParam(required = false) String code) {

        // 인가코드를 기반으로 토큰(Access Token) 발급
        String accessToken = kakaoService.getAccessToken(code);


        // 토큰을 통해 사용자 정보 조회후 토큰 발급
        // 재로그인인 경우 access token, 회원가입인 경우 temporary token

        String clientId = kakaoService.getClientId(accessToken);

        String token = authService.generateToken(clientId);

        HttpHeaders headers = authService.addTokenAtHeader(token);

        AuthResponseDTO.SocialLoginDTO socialLoginDTO = authService.socialLogin(clientId);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.onSuccess(socialLoginDTO));

    }

}
