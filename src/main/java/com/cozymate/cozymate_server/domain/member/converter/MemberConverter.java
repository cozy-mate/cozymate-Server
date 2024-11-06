package com.cozymate.cozymate_server.domain.member.converter;


import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.university.University;

public class MemberConverter {
    public static Member toMember(
            String clientId,
            MemberRequestDTO.SignUpRequestDTO signUpRequestDTO,
            University university
    ) {

        return Member.builder()
                .clientId(clientId)
                .socialType(ClientIdMaker.getSocialTypeInClientId(clientId))
                .role(Role.USER)
                .nickname(signUpRequestDTO.getNickname())
                .gender(Gender.getValue(signUpRequestDTO.getGender()))
                .birthDay(signUpRequestDTO.getBirthday())
                .persona(signUpRequestDTO.getPersona())
                .university(university)
                .build();
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {

        return MemberResponseDTO.MemberInfoDTO.builder()
                .nickname(member.getNickname())
                .gender(member.getGender().toString())
                .birthday(member.getBirthDay().toString())
                .universityName(member.getUniversity().getName())
                .majorName(member.getMajorName())
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