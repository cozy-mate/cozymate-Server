package com.cozymate.cozymate_server.domain.fcm.converter;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmRequestDto;
import com.cozymate.cozymate_server.domain.member.Member;

public class FcmConverter {

    public static Fcm toFcm(Member member, FcmRequestDto fcmRequestDto) {
        return Fcm.builder()
            .id(fcmRequestDto.getDeviceId())
            .token(fcmRequestDto.getToken())
            .member(member)
            .build();
    }
}