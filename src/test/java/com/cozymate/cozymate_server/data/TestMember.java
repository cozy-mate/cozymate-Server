package com.cozymate.cozymate_server.data;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;

public class TestMember {

    private static final String MEMBER_NAME = "홍길동";
    private static final Role ROLE = Role.USER;

    private static final SocialType SOCIAL_TYPE = SocialType.KAKAO;
    private static final Gender GENDER = Gender.MALE;
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 20);
    private static final String MAJOR_NAME = "컴퓨터공학과";

    private MemberRepository memberRepository;
    private UniversityRepository universityRepository; // 필요시 추가

    public TestMember(MemberRepository memberRepository, UniversityRepository universityRepository) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
    }

    /*
     * 기본 데이터 추가 method
     */
    @PostConstruct
    public void init() {
        University university = universityRepository.findById(1L).orElseThrow();
        Member member = createTestMember(university);
        memberRepository.save(member);
    }

    public Member createTestMember(University university) {
        return Member.builder()
                .socialType(SOCIAL_TYPE)
                .role(ROLE)
                .clientId("testClientId123")
                .nickname(MEMBER_NAME)
                .gender(GENDER)
                .birthDay(BIRTHDAY)
                .persona(5)
                .university(university) // University는 null
                .majorName(MAJOR_NAME)
                .memberStat(null) // MemberStat은 null
                .build();
    }
}

