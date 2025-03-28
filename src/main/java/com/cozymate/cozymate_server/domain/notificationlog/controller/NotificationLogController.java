package com.cozymate.cozymate_server.domain.notificationlog.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.notificationlog.dto.response.NotificationLogResponseDTO;
import com.cozymate.cozymate_server.domain.notificationlog.service.NotificationLogQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notificationLogs")
public class NotificationLogController {

    private final NotificationLogQueryService notificationLogQueryService;

    @GetMapping
    @Operation(summary = "[베로] 알림 내역 조회 (수정 - 25.03.26)", description = "")
    public ResponseEntity<ApiResponse<PageResponseDto<List<NotificationLogResponseDTO>>>> getNotificationLog(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            notificationLogQueryService.getNotificationLogList(memberDetails.member(), page, size)));
    }
}