package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import lombok.Getter;

@Getter
public class NotificationContentVO {

    private final Member member;
    private final String roomName;

    private NotificationContentVO(Member member) {
        this.member = member;
        roomName = null;
    }

    private NotificationContentVO(String roomName) {
        member = null;
        this.roomName = roomName;
    }

    public static NotificationContentVO create(Member member) {
        return new NotificationContentVO(member);
    }

    public static NotificationContentVO create(String roomName) {
        return new NotificationContentVO(roomName);
    }
}