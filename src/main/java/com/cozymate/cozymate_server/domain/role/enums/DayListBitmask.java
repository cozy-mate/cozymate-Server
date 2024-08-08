package com.cozymate.cozymate_server.domain.role.enums;

import java.util.List;

public enum DayListBitmask {
    월(1),
    화(2),
    수(4),
    목(8),
    금(16),
    토(32),
    일(64);

    private int value;

    DayListBitmask(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
