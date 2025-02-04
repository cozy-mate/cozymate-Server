package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;

import com.cozymate.cozymate_server.domain.university.University;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@SuppressWarnings("NonAsciiCharacters")
public class MemberFixture {

    private static final SocialType DEFAULT_SOCIAL_TYPE = SocialType.KAKAO;

    public static Member 정상_1(University university) {
        return 멤버_생성(1L, university, Gender.MALE, "테스트닉네임1",
            LocalDate.of(2000, 11, 1), "컴퓨터공학과");
    }

    public static Member 정상_2(University university) {
        return 멤버_생성(2L, university, Gender.MALE, "테스트닉네임2",
            LocalDate.of(2001, 11, 1), "경영학과");

    }

    public static Member 정상_3(University university) {
        return 멤버_생성(3L, university, Gender.FEMALE, "테스트닉네임3",
            LocalDate.of(2000, 11, 1), "컴퓨터공학과");
    }

    public static Member 정상_4(University university) {
        return 멤버_생성(4L, university, Gender.FEMALE, "테스트닉네임4",
            LocalDate.of(2001, 11, 1), "컴퓨터공학과");
    }


    /**
     * 다수의 테스트 남성멤버를 생성하고 저장
     *
     * @param university 소속 대학교
     * @param count      생성할 멤버 수
     * @return 생성된 멤버 리스트 생성된 멤버 : 00 년생 컴퓨터공학과 반, 01년생 경영학과 반
     */
    public static List<Member> 정상_남성_리스트(University university, int count) {
        List<Member> members = new ArrayList<>();

        members.addAll(리스트_커스텀(university, count / 2, Gender.MALE, LocalDate.of(2000, 1, 1), "컴퓨터공학과"));
        members.addAll(리스트_커스텀(university, count - (count / 2), Gender.MALE, LocalDate.of(2001, 1, 1), "행정학과"));

        return members;
    }

    /**
     * 다수의 테스트 여성멤버를 생성하고 저장
     *
     * @param university 소속 대학교
     * @param count      생성할 멤버 수
     * @return 생성된 멤버 리스트 생성된 멤버 : 00 년생 컴퓨터공학과 반, 01년생 경영학과 반
     */
    public static List<Member> 정상_여성_리스트(University university, int count) {
        List<Member> members = new ArrayList<>();

        members.addAll(리스트_커스텀(university, count / 2, Gender.FEMALE, LocalDate.of(2000, 1, 1), "컴퓨터공학과"));
        members.addAll(리스트_커스텀(university, count - (count / 2), Gender.FEMALE, LocalDate.of(2001, 1, 1), "행정학과"));

        return members;
    }

    public static List<Member> 리스트_커스텀(University university, int count, Gender gender, LocalDate startDate, String majorName) {
        List<Member> members = new ArrayList<>();

        IntStream.range(0, count).forEach(i ->
            members.add(멤버_생성((long) i + 1, university, gender, "testUser" + (i + 1), startDate.plusDays(i), majorName))
        );

        return members;
    }

    private static Member 멤버_생성(Long id, University university, Gender gender, String nickname,
        LocalDate birthDay, String majorName) {
        return Member.builder()
            .id(id)
            .socialType(DEFAULT_SOCIAL_TYPE)
            .role(Role.USER)
            .clientId("clientId_" + nickname + ":" + SocialType.KAKAO.toString())
            .nickname(nickname)
            .gender(gender)
            .birthDay(birthDay)
            .persona(5)
            .university(university)
            .majorName(majorName)
            .memberStat(null)
            .build();
    }


}
