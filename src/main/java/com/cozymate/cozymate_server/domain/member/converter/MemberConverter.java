package com.cozymate.cozymate_server.domain.member.converter;


import com.cozymate.cozymate_server.auth.dto.response.TokenResponseDTO;
import com.cozymate.cozymate_server.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberUniversityInfoResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.university.University;
import java.time.LocalDate;

public class MemberConverter {
    public static Member toPreMember(
        String clientId,
        University university,
        String majorName
    ) {

        return Member.builder()
            .clientId(clientId)
            .socialType(ClientIdMaker.getSocialTypeInClientId(clientId))
            .role(Role.PRE_USER)
            .university(university)
            .majorName(majorName)
            .build();
    }


    public static SignInResponseDTO toSignInResponseDTO(
        MemberDetailResponseDTO memberDetailResponseDTO,
        TokenResponseDTO tokenResponseDTO
    ) {
        return SignInResponseDTO.builder()
            .memberDetailResponseDTO(memberDetailResponseDTO)
            .tokenResponseDTO(tokenResponseDTO)
            .build();
    }

    public static SignInResponseDTO toTemporarySignInResponseDTO(
        TokenResponseDTO tokenResponseDTO) {
        return SignInResponseDTO.builder()
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
            member.getUniversity().getId(),
            member.getMajorName(),
            member.getPersona()
        );
    }

    public static MemberDetailResponseDTO toMemberDetailResponseDTOWithParams(
        Long memberId,
        String nickname, Gender gender,
        LocalDate birthday, String universityName,
        Long universityId,
        String majorName,
        Integer persona) {
        return MemberDetailResponseDTO.builder()
            .memberId(memberId)
            .nickname(nickname)
            .gender(makeString(gender))
            .birthday(makeString(birthday))
            .universityName(universityName)
            .universityId(universityId)
            .majorName(majorName)
            .persona(persona)
            .build();
    }

    public static MemberUniversityInfoResponseDTO toMemberUniversityInfoResponseDTO(
        String universityName,
        String mailAddress,
        String majorName
    ){
        return MemberUniversityInfoResponseDTO.builder()
            .universityName(universityName)
            .mailAddress(mailAddress)
            .majorName(majorName)
            .build();
    }

    private static String makeString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

}