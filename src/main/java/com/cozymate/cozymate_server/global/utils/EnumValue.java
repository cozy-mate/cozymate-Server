package com.cozymate.cozymate_server.global.utils;

import java.util.Arrays;

public interface EnumValue {
    static <E extends Enum<E> & EnumValue> E getValue(Class<E> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
            .filter(e -> e.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("잘못된 enum 값입니다: " + value));
    }
}
