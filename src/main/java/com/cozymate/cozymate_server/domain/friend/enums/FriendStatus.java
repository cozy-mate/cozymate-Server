package com.cozymate.cozymate_server.domain.friend.enums;

import lombok.Getter;

@Getter
public enum FriendStatus {
    WAITING("WAITING"), ACCEPT("ACCEPT");

    private final String friendStatus;

    FriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }
}
