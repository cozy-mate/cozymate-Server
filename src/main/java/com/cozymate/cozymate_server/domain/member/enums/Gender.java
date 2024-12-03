package com.cozymate.cozymate_server.domain.member.enums;


import com.cozymate.cozymate_server.global.utils.EnumValue;

public enum Gender implements EnumValue {

    MALE,
    FEMALE;

    @Override
    public String toString() {
        return name();
    }


    public static Gender getValue(String genderString) {
        return EnumValue.getValue(Gender.class, genderString);
    }
}
