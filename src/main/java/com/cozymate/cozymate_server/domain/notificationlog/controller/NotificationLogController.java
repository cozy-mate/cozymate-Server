package com.cozymate.cozymate_server.domain.notificationlog.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogResponseDto;
import com.cozymate.cozymate_server.domain.notificationlog.service.NotificationLogQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notificationLogs")
public class NotificationLogController {

    private final NotificationLogQueryService notificationLogQueryService;

    @GetMapping
    @Operation(summary = "[베로] 알림 내역 조회", description = "")
    public ResponseEntity<ApiResponse<List<NotificationLogResponseDto>>> getNotificationLog(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            notificationLogQueryService.getNotificationLogList(memberDetails.member())));
    }
}