package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.enums;

import lombok.Getter;

@Getter
public enum DifferenceStatus {
    // 방 안의 멤버 스탯들이 어떤 상태인지 표현하는 Status
    // BLUE : 모두 일치하는 일치율 항목
    // RED : 모두 다른 일치율 항목
    // WHITE : 모두 같지도, 모두 다르지도 않는 일치율 항목
    DIFFERENT("red"),
    SAME("blue"),
    NOT_SAME_NOT_DIFFERENT("white");

    private final String value;

    DifferenceStatus(String value) {
        this.value = value;
    }


}
