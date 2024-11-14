package com.cozymate.cozymate_server.domain.fcm.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.request.FcmRequestDTO;
import com.cozymate.cozymate_server.domain.fcm.service.FcmCommandService;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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


    private final MemberRepository memberRepository;
    private final FcmPushService fcmPushService;

    @GetMapping("/test/send")
    public ResponseEntity<ApiResponse<String>> sendTest() {
        Member member = memberRepository.findById(7L).get();

        fcmPushService.sendNotification(OneTargetDto.create(member, NotificationType.SELECT_COZY_MATE));

        return ResponseEntity.ok(ApiResponse.onSuccess(member.getNickname() + "에게 알림 전송"));
    }
}