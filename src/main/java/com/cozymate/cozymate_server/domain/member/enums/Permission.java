package com.cozymate.cozymate_server.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    USER_PUBLIC_ROOM_JOIN("user:public_room_join"),
    USER_PUBLIC_ROOM_UPDATE("user:public_room_update"),
    USER_PUBLIC_ROOM_CREATE("user:public_room_create"),
    USER_PUBLIC_ROOM_DELETE("user:public_room_delete"),

    ;


    @Getter
    private final String permission;
}
