package com.cozymate.cozymate_server.domain.auth.controller;



import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.dto.request.SignInRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.global.response.ApiResponse;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
    ResponseEntity<ApiResponse<SignInResponseDTO>> reissue(
            @RequestAttribute("refresh") String refreshToken
    ) {
        SignInResponseDTO signInResponseDTO = authService.reissue(refreshToken);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }

    @PostMapping("/sign-in")
    @Operation(summary = "[말즈] (수정 2025.3.27) 로그인",
        description = "`request Body : \"client_id\": \"123123\"`<br>"
            + "         *     `\"social_type\": \"KAKAO\"`<br>")
    @SwaggerApiError({
        ErrorStatus._MEMBER_BINDING_FAIL,
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signIn(
        @Valid @RequestBody SignInRequestDTO signInRequestDTO
    ) {

        SignInResponseDTO signInResponseDTO = authService.signIn(signInRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }
    @GetMapping("/logout")
    @Operation(summary = "[말즈] 로그아웃",
        description = "사용자를 로그아웃 시킵니다. 스웨거에서는 동작하지 않습니다!")
    @Deprecated
    public void signOut() {
    }
}
