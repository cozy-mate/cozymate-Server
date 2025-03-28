package com.cozymate.cozymate_server.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    PRE_USER_SIGNUP("user:signup"),
    ;


    @Getter
    private final String permission;
}
