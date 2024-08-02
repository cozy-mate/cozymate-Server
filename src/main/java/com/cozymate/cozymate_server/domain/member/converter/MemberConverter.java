package com.cozymate.cozymate_server.domain.member.converter;

import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;

public class MemberConverter {
    public static Member toMember(String clientId,
                                  MemberRequestDTO.JoinRequestDTO joinRequestDTO) {
        return Member.builder()
                .clientId(clientId)
                .socialType(ClientIdMaker.getSocialTypeInClientId(clientId))
                .role(Role.USER)
                .name(joinRequestDTO.getName())
                .nickname(joinRequestDTO.getNickName())
                .gender(Gender.valueOf(joinRequestDTO.getGender()))
                .birthDay(joinRequestDTO.getBirthday())
                .persona(joinRequestDTO.getPersona())
                .build();
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {
        return MemberResponseDTO.MemberInfoDTO.builder()
                .nickname(member.getNickname())
                .gender(member.getGender().toString())
                .birthDay(member.getBirthDay().toString())
                .persona(member.getPersona())
                .build();
    }

    public static MemberResponseDTO.LoginResponseDTO toLoginResponseDTO(
            String nickname, String refreshToken) {
        return MemberResponseDTO.LoginResponseDTO.builder()
                .nickname(nickname)
                .refreshToken(refreshToken)
                .build();
    }
}
