package com.cozymate.cozymate_server.domain.memberstat.enums;

import lombok.Getter;

@Getter
public enum DifferenceStatus {
    // BLUE : 모두 일치하는 일치율 항목
    // RED : 모두 다른 일치율 항목
    // WHITE : 모두 같지도, 모두 다르지도 않는 일치율 항목
    RED("red"),
    BLUE("blue"),
    WHITE("white");

    private final String value;

    DifferenceStatus(String value) {
        this.value = value;
    }

}
