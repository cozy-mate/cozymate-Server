package com.cozymate.cozymate_server.domain.fcm.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.request.FcmRequestDTO;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.fcm.service.FcmCommandService;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmCommandService fcmCommandService;
    private final FcmPushService fcmPushService;

    @PostMapping
    @Operation(summary = "[베로] fcm토큰, 기기 고유 값 저장", description = "body에 fcm토큰 값과 기기 고유 값을 넘겨주세요")
    public ResponseEntity<ApiResponse<String>> createFcm(
        @AuthenticationPrincipal MemberDetails memberDetails, @Valid @RequestBody
    FcmRequestDTO fcmRequestDTO) {
        fcmCommandService.createFcm(memberDetails.member(), fcmRequestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess("fcm토큰 및 기기 고유 값 저장 완료"));
    }




    private final FcmRepository fcmRepository;
    private static final int BATCH_SIZE = 500;

    @Deprecated
    @PostMapping("/send/all/test")
    @Operation(summary = "[베로] 공지 알림 전체 인원에게 전송 테스트용", description = "")
    public ResponseEntity<ApiResponse<String>> sendNoticeAllTest() {
        String content = "이게 머여.. 어억? 오옹? 치킨..? 이거 cozymate 출시 이벤트 아니에유?";
        Long noticeId = 1L;

        List<Fcm> fcmList = fcmRepository.findAllByIsValidIsTrue();

        List<List<Fcm>> batchList = new ArrayList<>();
        for (int i = 0; i < fcmList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, fcmList.size());
            batchList.add(fcmList.subList(i, end));
        }

        for (List<Fcm> batch : batchList) {
            log.info("컨트롤러에서 현재 스레드 이름 (시작): {}", Thread.currentThread().getName());
            fcmPushService.sendMulticastNotification(batch, content, noticeId);
            log.info("컨트롤러에서 현재 스레드 이름 (끝): {}", Thread.currentThread().getName());
        }

        return ResponseEntity.ok(ApiResponse.onSuccess("공지사항 알림 전송 및 저장 완료"));
    }

    @Deprecated
    @PostMapping("/send/me/test")
    @Operation(summary = "[베로] 공지 알림 자신에게 전송 테스트용", description = "")
    public ResponseEntity<ApiResponse<String>> sendNoticeTest(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        String content = "이게 머여.. 어억? 오옹? 치킨..? 이거 cozymate 출시 이벤트 아니에유?";
        Long noticeId = 1L;

        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(memberDetails.member());
        List<List<Fcm>> batchList = new ArrayList<>();
        Fcm fcm = fcmList.get(0);

        for (int i = 0; i < 5; i++) {
            List<Fcm> myFcm = new ArrayList<>();
            myFcm.add(fcm);
            batchList.add(myFcm);
        }

        for (List<Fcm> batch : batchList) {
            log.info("컨트롤러에서 현재 스레드 이름 (시작): {}", Thread.currentThread().getName());
            fcmPushService.sendMulticastNotification(batch, content, noticeId);
            log.info("컨트롤러에서 현재 스레드 이름 (종료): {}", Thread.currentThread().getName());
        }

        return ResponseEntity.ok(ApiResponse.onSuccess("공지사항 알림 전송 및 저장 완료"));
    }
}