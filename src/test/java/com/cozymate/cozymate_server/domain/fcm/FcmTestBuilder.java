package com.cozymate.cozymate_server.domain.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import java.time.LocalDate;

public class FcmTestBuilder {

    public static Member testMemberBuild() {
        return Member.builder()
            .nickname("베")
            .socialType(SocialType.APPLE)
            .role(Role.USER)
            .clientId("aaa")
            .name("aaa")
            .birthDay(LocalDate.now())
            .persona(1)
            .gender(Gender.MALE)
            .build();
    }

    public static Fcm testIPhoneFcmBuild() {
        return Fcm.builder()
            .id("iphone")
            .token("dummy-token")
            .member(testMemberBuild())
            .build();
    }

    public static Fcm testIPadFcmBuild() {
        return Fcm.builder()
            .id("ipad")
            .token("dummy-token")
            .member(testMemberBuild())
            .build();
    }

    public static Member testMember2Build() {
        return Member.builder()
            .nickname("로")
            .socialType(SocialType.APPLE)
            .role(Role.USER)
            .clientId("aaa")
            .name("aaa")
            .birthDay(LocalDate.now())
            .persona(1)
            .gender(Gender.MALE)
            .build();
    }

    public static Fcm testIPhoneFcm2Build() {
        return Fcm.builder()
            .id("iphone2")
            .token("dummy-token")
            .member(testMember2Build())
            .build();
    }

    public static Fcm testIPadFcm2Build() {
        return Fcm.builder()
            .id("ipad2")
            .token("dummy-token")
            .member(testMember2Build())
            .build();
    }
}