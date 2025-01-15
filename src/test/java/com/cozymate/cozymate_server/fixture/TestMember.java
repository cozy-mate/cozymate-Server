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

public class TestMember {

    private static final Role DEFAULT_ROLE = Role.USER;
    private static final SocialType DEFAULT_SOCIAL_TYPE = SocialType.KAKAO;


    /**
     * 다수의 테스트 멤버를 생성하고 저장
     *
     * @param university 소속 대학교
     * @param count 생성할 멤버 수
     * @return 생성된 멤버 리스트
     */
    public static List<Member> 정상_남성_리스트(University university, int count) {
        List<Member> members = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            String nickname = "testUser" + (i + 1);
            Member member = Member.builder()
                .socialType(DEFAULT_SOCIAL_TYPE)
                .role(DEFAULT_ROLE)
                .clientId("clientId_" + nickname)
                .nickname(nickname)
                .gender(Gender.MALE)
                .birthDay(LocalDate.of(2000, 1, 1).plusDays(i))
                .persona(5)
                .university(university)
                .majorName("컴퓨터공학과")
                .memberStat(null)
                .build();

            members.add(member);
        });
        return members;
    }

    public static List<Member> 정상_여성_리스트(University university, int count) {
        List<Member> members = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            String nickname = "testUser" + (i + 1);
            Member member = Member.builder()
                .socialType(DEFAULT_SOCIAL_TYPE)
                .role(DEFAULT_ROLE)
                .clientId("clientId_" + nickname)
                .nickname(nickname)
                .gender(Gender.FEMALE)
                .birthDay(LocalDate.of(2000, 1, 1).plusDays(i))
                .persona(5)
                .university(university)
                .majorName("컴퓨터공학과")
                .memberStat(null)
                .build();

            members.add(member);
        });
        return members;
    }
}
