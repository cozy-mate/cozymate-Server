package com.cozymate.cozymate_server.domain.member.converter;


import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.university.University;
import java.time.LocalDate;

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
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO) {

        return MemberResponseDTO.SignInResponseDTO.builder()
            .tokenResponseDTO(tokenResponseDTO)
            .build();
    }

    // 이런식으로 엔티티를 DTO로 만들 땐 From Entity로 이름을 붙여주시고요, WithParams로 파라미터로 만드는 Converter도 만들어주면 좋습니다.
    public static MemberDetailResponseDTO toMemberDetailResponseDTOFromEntity(Member member) {
        return toMemberDetailResponseDTOWithParams(
            member.getId(),
            member.getNickname(),
            member.getGender(),
            member.getBirthDay(),
            member.getUniversity().getName(),
            member.getMajorName(),
            member.getPersona()
        );
    }

    public static MemberDetailResponseDTO toMemberDetailResponseDTOWithParams(Long memberId,
        String nickname, Gender gender, LocalDate birthday, String universityName, String majorName,
        Integer persona) {
        return MemberDetailResponseDTO.builder()
            .memberId(memberId)
            .nickname(nickname)
            .gender(gender.toString())
            .birthday(birthday.toString())
            .universityName(universityName)
            .majorName(majorName)
            .persona(persona)
            .build();
    }

}