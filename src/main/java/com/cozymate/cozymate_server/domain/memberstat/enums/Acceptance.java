package com.cozymate.cozymate_server.domain.memberstat.enums;

public enum Acceptance {
    ACCEPT("합격"),
    WAITING("대기중"),
    SPARE_NUM("대기번호"),

    ;

    private String acceptance;

    Acceptance(String acceptance) {
        this.acceptance = acceptance;
    }
}
