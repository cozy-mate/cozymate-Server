package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;

@SuppressWarnings("NonAsciiCharacters")
public class NotificationLogFixture {

    // 정상 더미데이터, 카테고리가 "방"인 경우
    public NotificationLog 정상_1(Member member) {
        return NotificationLog.builder()
            .id(1L)
            .member(member)
            .category(NotificationCategory.ROOM)
            .content("테스트 알림 내용 1")
            .targetId(null) // 일단 null
            .build();
    }

    // 정상 더미데이터, 카테고리가 "방 참여 요청"인 경우
    public NotificationLog 정상_2(Member member) {
        return NotificationLog.builder()
            .id(2L)
            .member(member)
            .category(NotificationCategory.ROOM_JOIN_REQUEST)
            .content("테스트 알림 내용 2")
            .targetId(null) // 일단 null
            .build();
    }
}
