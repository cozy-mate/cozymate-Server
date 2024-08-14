package com.cozymate.cozymate_server.domain.member.converter;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;

public class MemberConverter {
    public static Member toMember(
            String clientId,
            MemberRequestDTO.SignUpRequestDTO signUpRequestDTO) {

        return Member.builder()
                .clientId(clientId)
                .socialType(ClientIdMaker.getSocialTypeInClientId(clientId))
                .role(Role.USER)
                .name(signUpRequestDTO.getName())
                .nickname(signUpRequestDTO.getNickname())
                .gender(Gender.valueOf(signUpRequestDTO.getGender()))
                .birthDay(signUpRequestDTO.getBirthday())
                .persona(signUpRequestDTO.getPersona())
                .build();
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {

        return MemberResponseDTO.MemberInfoDTO.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .gender(member.getGender().toString())
                .birthday(member.getBirthDay().toString())
                .persona(member.getPersona())
                .build();
    }

    public static MemberResponseDTO.SignInResponseDTO toLoginResponseDTO(
            MemberResponseDTO.MemberInfoDTO memberInfoDTO,
            AuthResponseDTO.TokenResponseDTO tokenResponseDTO) {

        return MemberResponseDTO.SignInResponseDTO.builder()
                .tokenResponseDTO(tokenResponseDTO)
                .memberInfoDTO(memberInfoDTO)
                .build();

    }

    public static MemberResponseDTO.SignInResponseDTO toTemporaryLoginResponseDTO(
            AuthResponseDTO.TokenResponseDTO tokenResponseDTO){

        return MemberResponseDTO.SignInResponseDTO.builder()
                .tokenResponseDTO(tokenResponseDTO)
                .build();
    }

}