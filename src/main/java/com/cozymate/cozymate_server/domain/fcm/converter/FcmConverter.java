package com.cozymate.cozymate_server.domain.fcm.converter;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.request.FcmRequestDTO;
import com.cozymate.cozymate_server.domain.member.Member;

public class FcmConverter {

    public static Fcm toEntity(Member member, FcmRequestDTO fcmRequestDTO) {
        return Fcm.builder()
            .id(member.getClientId())
            .token(fcmRequestDTO.token())
            .member(member)
            .isValid(true)
            .build();
    }
}