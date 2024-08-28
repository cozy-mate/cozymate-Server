package com.cozymate.cozymate_server.domain.notificationlog.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogResponseDto;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import com.cozymate.cozymate_server.domain.notificationlog.service.NotificationLogQueryService;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Operation(summary = "[베로] 알림 내역 조회", description = "파라미터에 알림 category enum값 중 아무거나")
    public ResponseEntity<ApiResponse<List<NotificationLogResponseDto>>> getNotificationLog(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(required = false) NotificationCategory notificationCategory) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            notificationLogQueryService.getNotificationLogList(memberDetails.getMember(),
                notificationCategory)));
    }

    /**
     * 아래 테스트용 API
     */

    private final FcmPushService fcmPushService;
    private final MateRepository mateRepository;

    @Deprecated
    @GetMapping("/test")
    @Operation(summary = "알림 테스트용")
    public String sendNotificationTest(@AuthenticationPrincipal MemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        fcmPushService.sendNotification(
            OneTargetDto.create(member, NotificationType.BEST_COZY_MATE));
        log.info("베스트 코지 메이트 선정 알림 전송 완료");
        return "알림 전송 완료";
    }

//    @Deprecated
//    @GetMapping("/test/todo")
//    @Operation(summary = "알림 테스트용")
//    public String sendTodoNotification(@AuthenticationPrincipal MemberDetails memberDetails) {
//        Member member = memberDetails.getMember();
//
//        //Optional<Mate> mate = mateRepository.findByMember(member);
//
//        List<String> todoList = new ArrayList<>();
//        todoList.add("설거지하기");
//        todoList.add("콩 밥주기");
//        todoList.add("밥 먹기");
//        todoList.add("버스 표 끊기");
//        fcmPushService.sendNotification(
//            OneTargetDto.create(member, NotificationType.TODO_LIST, todoList));
//        log.info("투두 리스트 알림 전송 완료");
//        return "알림 전송 완료";
//    }
}