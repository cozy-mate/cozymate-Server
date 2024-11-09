package com.cozymate.cozymate_server.domain.fcm.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.fcm.dto.request.FcmRequestDTO;
import com.cozymate.cozymate_server.domain.fcm.service.FcmCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmCommandService fcmCommandService;

    @PostMapping
    @Operation(summary = "[베로] fcm토큰, 기기 고유 값 저장", description = "body에 fcm토큰 값과 기기 고유 값을 넘겨주세요")
    public ResponseEntity<ApiResponse<String>> createFcm(
        @AuthenticationPrincipal MemberDetails memberDetails, @Valid @RequestBody
    FcmRequestDTO fcmRequestDTO) {
        fcmCommandService.createFcm(memberDetails.member(), fcmRequestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess("fcm토큰 및 기기 고유 값 저장 완료"));
    }
}