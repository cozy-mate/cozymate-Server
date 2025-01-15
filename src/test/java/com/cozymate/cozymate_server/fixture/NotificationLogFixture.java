package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;

public class NotificationLogFixture {

    private static final Long NOTIFICATION_LOG_ID_1 = 1L;
    private static final Long NOTIFICATION_LOG_ID_2 = 2L;

    private static final String NOTIFICATION_CONTENT_1 = "테스트 알림 내용 1";
    private static final String NOTIFICATION_CONTENT_2 = "테스트 알림 내용 2";

    public static NotificationLog buildNotificationLog1(Member member) {
        return NotificationLog.builder()
            .id(NOTIFICATION_LOG_ID_1)
            .member(member)
            .category(NotificationCategory.ROOM)
            .content(NOTIFICATION_CONTENT_1)
            .targetId(null) // 일단 null
            .build();
    }

    public static NotificationLog buildNotificationLog2(Member member) {
        return NotificationLog.builder()
            .id(NOTIFICATION_LOG_ID_2)
            .member(member)
            .category(NotificationCategory.ROOM_JOIN_REQUEST)
            .content(NOTIFICATION_CONTENT_2)
            .targetId(null) // 일단 null
            .build();
    }
}
