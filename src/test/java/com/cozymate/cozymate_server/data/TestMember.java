package com.cozymate.cozymate_server.data;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestMember {

    private static final Role DEFAULT_ROLE = Role.USER;
    private static final SocialType DEFAULT_SOCIAL_TYPE = SocialType.KAKAO;
    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final String DEFAULT_MAJOR_NAME = "컴퓨터공학과";


    /**
     * 다수의 테스트 멤버를 생성하고 저장
     *
     * @param university 소속 대학교
     * @param count 생성할 멤버 수
     * @return 생성된 멤버 리스트
     */
    public static final List<Member> createAndSaveTestMembers(University university, int count) {


        List<Member> members = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            String nickname = "testUser" + (i + 1);
            Member member = Member.builder()
                .socialType(DEFAULT_SOCIAL_TYPE)
                .role(DEFAULT_ROLE)
                .clientId("clientId_" + nickname)
                .nickname(nickname)
                .gender(DEFAULT_GENDER)
                .birthDay(LocalDate.of(2000, 1, 1).plusDays(i))
                .persona(5)
                .university(university)
                .majorName(DEFAULT_MAJOR_NAME)
                .memberStat(null)
                .build();

            members.add(member);
        });
        return members;
    }
}
