package com.cozymate.cozymate_server.domain.role.enums;

import java.time.DayOfWeek;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum DayListBitmask {
    월(1, DayOfWeek.MONDAY),
    화(2, DayOfWeek.TUESDAY),
    수(4, DayOfWeek.WEDNESDAY),
    목(8, DayOfWeek.THURSDAY),
    금(16, DayOfWeek.FRIDAY),
    토(32, DayOfWeek.SATURDAY),
    일(64, DayOfWeek.SUNDAY);

    private int value;
    private DayOfWeek dayOfWeek;

    DayListBitmask(int value, DayOfWeek dayOfWeek) {
        this.value = value;
        this.dayOfWeek = dayOfWeek;
    }

    public static int getBitmaskByDayOfWeek(DayOfWeek dayOfWeek) {
        return Arrays.stream(values())
            .filter(day -> day.getDayOfWeek() == dayOfWeek)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid DayOfWeek: " + dayOfWeek))
            .getValue();
    }
}