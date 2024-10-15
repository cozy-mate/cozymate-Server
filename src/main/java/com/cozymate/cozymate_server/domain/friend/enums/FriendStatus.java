package com.cozymate.cozymate_server.domain.friend.enums;

import lombok.Getter;

@Getter
public enum FriendStatus {
    STRANGER("STRANGER"),WAITING("WAITING"), ACCEPT("ACCEPT");

    private final String friendStatus;

    FriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }
}
