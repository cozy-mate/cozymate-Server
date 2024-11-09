package com.cozymate.cozymate_server.domain.auth.controller;



import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/reissue")
    @Operation(summary = "[말즈] 토큰 재발행",
            description = "request Header : Bearer refreshToken")
    ResponseEntity<ApiResponse<TokenResponseDTO>> reissue(
            @RequestAttribute("refresh") String refreshToken
    ) {
        TokenResponseDTO tokenResponseDTO = authService.reissue(refreshToken);

        return ResponseEntity.ok(ApiResponse.onSuccess(tokenResponseDTO));
    }
}
