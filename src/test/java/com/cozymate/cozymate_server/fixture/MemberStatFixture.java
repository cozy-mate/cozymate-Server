package com.cozymate.cozymate_server.fixture;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberUniversityStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("NonAsciiCharacters")
public class MemberStatFixture {

    private static Long ID_GENERATOR = 0L;

    /**
     * 기본적인 MemberStat 객체를 생성합니다.
     * @param member Member 객체
     * @return MemberStat 객체
     */
    public static MemberStat 정상_1(Member member) {
        return 멤버_스탯_생성(member, 기본_대학_스탯(), 기본_라이프스타일(), "안녕하세요, 자기소개입니다.");
    }

    /**
     * 두 번째 기본 MemberStat 객체를 생성합니다.
     * @param member Member 객체
     * @return MemberStat 객체
     */
    public static MemberStat 정상_2(Member member) {
        return 멤버_스탯_생성(member, 기본_대학_스탯(), 기본_라이프스타일2(), "안녕하세요, 자기소개입니다.");
    }

    /**
     * 커스텀 값을 이용하여 MemberStat 객체를 생성합니다.
     * @param member Member 객체
     * @param memberUniversityStat 대학 관련 통계 정보
     * @param lifestyle 생활 패턴 정보
     * @param selfIntroduction 자기소개
     * @return MemberStat 객체
     */
    public static MemberStat 커스텀(Member member, MemberUniversityStat memberUniversityStat,
        Lifestyle lifestyle, String selfIntroduction) {
        return 멤버_스탯_생성(member, memberUniversityStat, lifestyle, selfIntroduction);
    }


    /**
     * 익일에 기상하는 lifestyle 을 생성합니다.
     * @return MemberUniversityStat 객체
     */
    public static Lifestyle 큰_wakeUpTime(){
        return Lifestyle.builder()
            .wakeUpTime(25)
            .sleepingTime(23)
            .turnOffTime(22)
            .smokingStatus(0)
            .sleepingHabit(0)
            .coolingIntensity(2)
            .heatingIntensity(2)
            .lifePattern(1)
            .intimacy(1)
            .itemSharing(2)
            .playingGameFrequency(1)
            .phoneCallingFrequency(1)
            .studyingFrequency(2)
            .eatingFrequency(3)
            .cleannessSensitivity(3)
            .noiseSensitivity(2)
            .cleaningFrequency(3)
            .drinkingFrequency(2)
            .personality(0b000000000001)
            .mbti(3)
            .build();
    }


    public static CreateMemberStatRequestDTO 정상_생성_요청_DTO() {
        return CreateMemberStatRequestDTO.builder()
            .admissionYear("23")
            .numOfRoommate(2)
            .dormitoryName("기숙사 A")
            .acceptance("합격")
            .wakeUpMeridian("오전")
            .wakeUpTime(7)
            .sleepingMeridian("오전")
            .sleepingTime(1)
            .turnOffMeridian("오전")
            .turnOffTime(12)
            .smoking("비흡연자")
            .sleepingHabit(List.of("코골이", "뒤척임"))
            .airConditioningIntensity(2)
            .heatingIntensity(2)
            .lifePattern("아침형 인간")
            .intimacy("어느정도 친하게 지내요")
            .canShare("휴지정도는 빌려줄 수 있어요")
            .isPlayGame("보이스 채팅도 해요")
            .isPhoneCall("급한 전화만 해요")
            .studying("매일 해요")
            .intake("배달음식도 먹어요")
            .cleanSensitivity(3)
            .noiseSensitivity(3)
            .cleaningFrequency("일주일에 한 번 해요")
            .drinkingFrequency("한 달에 한 두번 마셔요")
            .personality(List.of("조용해요", "부지런해요"))
            .mbti("INTJ")
            .selfIntroduction("안녕하세요, 저는 조용한 사람입니다.")
            .build();
    }


    public static CreateMemberStatRequestDTO 실패_잘못된_학번_입력() {
        return 정상_생성_요청_DTO().toBuilder()
            .admissionYear("TwentyOne") // 숫자로 변환할 수 없는 값
            .build();
    }

    public static CreateMemberStatRequestDTO 실패_잘못된_smoking() {
        return 정상_생성_요청_DTO().toBuilder()
            .smoking("존재하지 않는 흡연 상태") // `QuestionAnswerMapper`에서 찾을 수 없는 값
            .build();
    }

    public static CreateMemberStatRequestDTO 실패_잘못된_lifePattern() {
        return 정상_생성_요청_DTO().toBuilder()
            .lifePattern("이상한 라이프스타일") // `QuestionAnswerMapper`에서 찾을 수 없는 값
            .build();
    }

    public static CreateMemberStatRequestDTO 실패_잘못된_mbti() {
        return 정상_생성_요청_DTO().toBuilder()
            .mbti("XXXX") // 존재하지 않는 MBTI 값
            .build();
    }

    /**
     * MemberStat 객체를 생성하는 내부 메서드입니다.
     * @param member Member 객체
     * @param memberUniversityStat 대학 관련 통계 정보
     * @param lifestyle 생활 패턴 정보
     * @param selfIntroduction 자기소개
     * @return MemberStat 객체
     */
    private static MemberStat 멤버_스탯_생성(Member member, MemberUniversityStat memberUniversityStat,
        Lifestyle lifestyle, String selfIntroduction) {
        return MemberStat.builder()
            .id(ID_GENERATOR++)
            .member(member)
            .memberUniversityStat(memberUniversityStat)
            .lifestyle(lifestyle)
            .selfIntroduction(selfIntroduction)
            .build();
    }

    /**
     * 기본 대학 스탯 정보를 생성합니다.
     * @return MemberUniversityStat 객체
     */
    private static MemberUniversityStat 기본_대학_스탯() {
        return MemberUniversityStat.builder()
            .admissionYear(2020)
            .dormitoryName("기숙사 A")
            .numberOfRoommate("2인실")
            .acceptance("합격")
            .build();
    }

    /**
     * 기본 생활 패턴 정보를 생성합니다.
     * @return Lifestyle 객체
     */
    private static Lifestyle 기본_라이프스타일() {
        return Lifestyle.builder()
            .wakeUpTime(7)
            .sleepingTime(23)
            .turnOffTime(22)
            .smokingStatus(0)
            .sleepingHabit(0)
            .coolingIntensity(2)
            .heatingIntensity(2)
            .lifePattern(1)
            .intimacy(1)
            .itemSharing(2)
            .playingGameFrequency(1)
            .phoneCallingFrequency(1)
            .studyingFrequency(2)
            .eatingFrequency(3)
            .cleannessSensitivity(3)
            .noiseSensitivity(2)
            .cleaningFrequency(3)
            .drinkingFrequency(2)
            .personality(0b000000000001)
            .mbti(3)
            .build();
    }

    /**
     * 두 번째 기본 생활 패턴 정보를 생성합니다.
     * @return Lifestyle 객체
     */
    private static Lifestyle 기본_라이프스타일2() {
        return Lifestyle.builder()
            .wakeUpTime(10)
            .sleepingTime(2)
            .turnOffTime(1)
            .smokingStatus(1)
            .sleepingHabit(0b11010)
            .coolingIntensity(1)
            .heatingIntensity(1)
            .lifePattern(2)
            .intimacy(3)
            .itemSharing(4)
            .playingGameFrequency(2)
            .phoneCallingFrequency(2)
            .studyingFrequency(1)
            .eatingFrequency(2)
            .cleannessSensitivity(3)
            .noiseSensitivity(2)
            .cleaningFrequency(3)
            .drinkingFrequency(2)
            .personality(0b000000000001)
            .mbti(5)
            .build();
    }

    /**
     * 주어진 Member 리스트를 기반으로 랜덤 MemberStat 리스트를 생성합니다.
     * @param members Member 객체 리스트
     * @param count 생성할 MemberStat 객체 수
     * @param seed 랜덤 시드 값 (null일 경우 기본 랜덤 생성)
     * @return MemberStat 객체 리스트
     */
    public static List<MemberStat> 랜덤_멤버_스탯_리스트(List<Member> members, int count, Long seed) {
        List<MemberStat> memberStats = new ArrayList<>();
        Random random = (seed != null) ? new Random(seed) : new Random();
        ID_GENERATOR = (seed != null) ? seed : 0L;

        for (int i = 0; i < count; i++) {
            Member member = members.get(i % members.size());

            Lifestyle lifestyle = Lifestyle.builder()
                .wakeUpTime(random.nextInt(24))
                .sleepingTime(random.nextInt(24))
                .turnOffTime(random.nextInt(24))
                .smokingStatus(random.nextInt(4))
                .sleepingHabit(random.nextInt(32))
                .coolingIntensity(random.nextInt(4))
                .heatingIntensity(random.nextInt(4))
                .lifePattern(random.nextInt(2))
                .intimacy(random.nextInt(3))
                .itemSharing(random.nextInt(4))
                .playingGameFrequency(random.nextInt(3))
                .phoneCallingFrequency(random.nextInt(3))
                .studyingFrequency(random.nextInt(3))
                .eatingFrequency(random.nextInt(4))
                .cleannessSensitivity(random.nextInt(5))
                .noiseSensitivity(random.nextInt(5))
                .cleaningFrequency(random.nextInt(5))
                .drinkingFrequency(random.nextInt(5))
                .personality(1 + random.nextInt(4095))
                .mbti(random.nextInt(16))
                .build();

            String selfIntroduction = "안녕하세요, 저는 " + member.getNickname() + "입니다.";

            memberStats.add(멤버_스탯_생성(member, 기본_대학_스탯(), lifestyle, selfIntroduction));
        }

        return memberStats;
    }
}