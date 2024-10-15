package com.cozymate.cozymate_server.domain.fcm.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmPushContentResultDto {

    private String notificationContent; // 푸시 알림에 갈 문자열 값
    private String logContent; // 알림 로그에 저장될 문자열 값
}