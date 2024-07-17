package com.cozymate.cozymate_server.domain.notificationlog.enums;

public enum NotificationType {
    INVITE("초대 알림"),

    ;

    private String content;

    NotificationType(String content){
        this.content = content;
    }
}
