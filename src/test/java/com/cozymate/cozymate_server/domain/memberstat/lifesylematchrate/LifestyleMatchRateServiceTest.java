package com.cozymate.cozymate_server.domain.memberstat.lifesylematchrate;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository.LifestyleMatchRateRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.MemberStatFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LifestyleMatchRateServiceTest {

    @Mock
    private LifestyleMatchRateRepository lifestyleMatchRateRepository;
    @Mock
    private MemberStatRepository memberStatRepository;
    @Mock
    private UniversityRepository universityRepository;

    @InjectMocks
    private LifestyleMatchRateService lifestyleMatchRateService;

    @Nested
    @DisplayName("getMatchRateWithMemberIdAndIdList 테스트")
    class GetMatchRateWithMemberIdAndIdList {

        private Member memberA;
        private Member memberB;
        private List<Member> targetMembers;

        private List<Long> memberIdList;
        private List<LifestyleMatchRate.LifestyleMatchRateId> idList;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            memberA = MemberFixture.정상_1(university);
            memberB = MemberFixture.정상_2(university);
            targetMembers = MemberFixture.리스트_커스텀(university, 5, memberA.getGender(),
                LocalDate.now(), "컴퓨터공학과");

            memberIdList = List.of(memberB.getId());
            idList = memberIdList.stream()
                .map(id -> new LifestyleMatchRate.LifestyleMatchRateId(memberA.getId(), id))
                .toList();
        }

        @Test
        @DisplayName("주어진 ID 리스트에 대한 일치율을 반환")
        void success_should_return_match_rate_map() {
            // given
            LifestyleMatchRate matchRate = new LifestyleMatchRate(memberA.getId(), memberB.getId(),
                85);
            given(lifestyleMatchRateRepository.findByIdList(idList)).willReturn(List.of(matchRate));

            // when
            Map<Long, Integer> result = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                memberA.getId(), memberIdList);

            // then
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(memberB.getId())).isEqualTo(85);
        }

        @Test
        @DisplayName("여러 명의 멤버가 있는 경우, 올바르게 매칭 데이터를 반환")
        void success_should_return_match_rate_map_for_multiple_members() {
            // given
            List<LifestyleMatchRate> matchRates = targetMembers.stream()
                .map(member -> new LifestyleMatchRate(memberA.getId(), member.getId(), 80))
                .toList();

            given(lifestyleMatchRateRepository.findByIdList(idList)).willReturn(matchRates);

            // when
            Map<Long, Integer> result = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                memberA.getId(), memberIdList);

            // then
            assertThat(result.size()).isEqualTo(targetMembers.size());
            targetMembers.forEach(member -> assertThat(result.get(member.getId())).isEqualTo(80));
        }

    }

    @Nested
    @DisplayName("getMatchRate 테스트")
    class GetMatchRate {

        private Member memberA;
        private Member memberB;
        private List<Member> targetMembers;


        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            memberA = MemberFixture.정상_1(university);
            memberB = MemberFixture.정상_2(university);
            targetMembers = MemberFixture.리스트_커스텀(university, 3, memberA.getGender(),
                LocalDate.now(), "전자공학");

        }

        @Test
        @DisplayName("단일 멤버의 모든 일치율을 반환")
        void success_should_return_all_match_rates_for_member() {
            // given
            LifestyleMatchRate matchRate = new LifestyleMatchRate(memberA.getId(), memberB.getId(),
                75);
            given(lifestyleMatchRateRepository.findBySingleMemberId(memberA.getId())).willReturn(
                List.of(matchRate));

            // when
            Map<Long, Integer> result = lifestyleMatchRateService.getMatchRate(memberA.getId());

            // then
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(memberB.getId())).isEqualTo(75);
        }

        @Test
        @DisplayName("여러 명의 멤버에 대한 모든 일치율을 반환")
        void success_should_return_all_match_rates_for_multiple_members() {
            // given
            List<LifestyleMatchRate> matchRates = targetMembers.stream()
                .map(member -> new LifestyleMatchRate(memberA.getId(), member.getId(), 75))
                .toList();

            given(lifestyleMatchRateRepository.findBySingleMemberId(memberA.getId())).willReturn(
                matchRates);

            // when
            Map<Long, Integer> result = lifestyleMatchRateService.getMatchRate(memberA.getId());

            // then
            assertThat(result.size()).isEqualTo(targetMembers.size());
            targetMembers.forEach(member -> assertThat(result.get(member.getId())).isEqualTo(75));
        }
    }

    @Nested
    @DisplayName("getSingleMatchRate 테스트")
    class GetSingleMatchRate {

        private Member memberA;
        private Member memberB;
        private MemberStat memberStatA;
        private List<MemberStat> targetMemberStats;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            memberA = MemberFixture.정상_1(university);
            memberB = MemberFixture.정상_2(university);
            List<Member> targetMembers = MemberFixture.리스트_커스텀(university, 4, memberA.getGender(),
                LocalDate.now(), "경영학과");
            memberStatA = MemberStatFixture.정상_1(memberA);
            targetMemberStats = MemberStatFixture.랜덤_멤버_스탯_리스트(targetMembers, 4, 123L);

        }

        @Test
        @DisplayName("두 멤버 간의 일치율 반환")
        void success_should_return_match_rate_between_two_members() {
            // given
            LifestyleMatchRate matchRate = new LifestyleMatchRate(memberA.getId(), memberB.getId(),
                90);
            given(lifestyleMatchRateRepository.findById(
                new LifestyleMatchRate.LifestyleMatchRateId(memberA.getId(), memberB.getId())))
                .willReturn(Optional.of(matchRate));

            // when
            Integer result = lifestyleMatchRateService.getSingleMatchRate(memberA.getId(),
                memberB.getId());

            // then
            assertThat(result).isEqualTo(90);
        }

        @Test
        @DisplayName("여러 명의 멤버에 대해 일치율을 계산하고 저장")
        void success_should_calculate_and_save_match_rate_for_multiple_members() {
            // given
            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                memberStatA.getMember().getGender(),
                memberStatA.getMember().getUniversity().getId(),
                memberStatA.getMember().getId()))
                .willReturn(targetMemberStats);

            targetMemberStats.forEach(targetStat -> {
                given(lifestyleMatchRateRepository.findById(
                    new LifestyleMatchRate.LifestyleMatchRateId(
                        memberStatA.getMember().getId(), targetStat.getMember().getId())))
                    .willReturn(Optional.empty());
            });

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            verify(lifestyleMatchRateRepository, times(targetMemberStats.size())).save(
                any(LifestyleMatchRate.class));
        }

    }

    @Nested
    @DisplayName("saveLifeStyleMatchRate 테스트")
    class SaveLifeStyleMatchRate {

        private MemberStat memberStatA;
        private MemberStat memberStatB;
        private Member memberA;
        private Member memberB;
        private List<MemberStat> targetMemberStats;


        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            memberA = MemberFixture.정상_1(university);
            memberB = MemberFixture.정상_2(university);
            memberStatA = MemberStatFixture.정상_1(memberA);
            memberStatB = MemberStatFixture.정상_1(memberB);

            targetMemberStats = MemberStatFixture.랜덤_멤버_스탯_리스트(
                MemberFixture.리스트_커스텀(university, 4, memberA.getGender(), LocalDate.now(),
                    "컴퓨터공학과"),
                4, 123L);
        }

        @Test
        @DisplayName("일치율이 계산되고 저장되는지 검증")
        void success_should_calculate_and_save_match_rate() {
            // given
            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                memberStatA.getMember().getGender(),
                memberStatA.getMember().getUniversity().getId(),
                memberStatA.getMember().getId()))
                .willReturn(List.of(memberStatB));

            given(lifestyleMatchRateRepository.findById(new LifestyleMatchRate.LifestyleMatchRateId(
                memberStatA.getMember().getId(), memberStatB.getMember().getId())))
                .willReturn(Optional.empty());

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            verify(lifestyleMatchRateRepository, times(1)).save(any(LifestyleMatchRate.class));
        }

        @Test
        @DisplayName("완전히 일치하는 라이프스타일의 경우 일치율이 100으로 저장")
        void success_should_calculate_100_when_lifestyle_is_fully_matched() {
            // given
            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                memberStatA.getMember().getGender(),
                memberStatA.getMember().getUniversity().getId(),
                memberStatA.getMember().getId()))
                .willReturn(List.of(memberStatB));

            given(lifestyleMatchRateRepository.findById(new LifestyleMatchRate.LifestyleMatchRateId(
                memberStatA.getMember().getId(), memberStatB.getMember().getId())))
                .willReturn(Optional.empty());

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<LifestyleMatchRate> captor = ArgumentCaptor.forClass(
                LifestyleMatchRate.class);
            verify(lifestyleMatchRateRepository, times(1)).save(captor.capture());

            LifestyleMatchRate savedRate = captor.getValue();
            assertThat(savedRate.getMatchRate()).isEqualTo(100);
        }

        @Test
        @DisplayName("부분적으로 일치하는 라이프스타일의 경우 예상 일치율을 저장")
        void success_should_calculate_expected_match_rate_when_lifestyle_partially_matches() {
            // given
            MemberStat partialMatchedStat = MemberStatFixture.정상_비교용(memberB); // 부분 일치하는 데이터 생성

            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                memberStatA.getMember().getGender(),
                memberStatA.getMember().getUniversity().getId(),
                memberStatA.getMember().getId()))
                .willReturn(List.of(partialMatchedStat));

            given(lifestyleMatchRateRepository.findById(new LifestyleMatchRate.LifestyleMatchRateId(
                memberStatA.getMember().getId(), partialMatchedStat.getMember().getId())))
                .willReturn(Optional.empty());

            // 예상되는 일치율 (테스트 데이터를 기반으로 변경 가능)
            int expectedMatchRate = 91; // 응답 개수

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<LifestyleMatchRate> captor = ArgumentCaptor.forClass(
                LifestyleMatchRate.class);
            verify(lifestyleMatchRateRepository, times(1)).save(captor.capture());

            LifestyleMatchRate savedRate = captor.getValue();
            assertThat(savedRate.getMatchRate()).isEqualTo(expectedMatchRate);
        }

        @Test
        @DisplayName("여러 명의 멤버에 대해 일치율을 계산하고 저장")
        void success_should_calculate_and_save_match_rate_for_multiple_members() {
            // given
            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                memberStatA.getMember().getGender(),
                memberStatA.getMember().getUniversity().getId(),
                memberStatA.getMember().getId()))
                .willReturn(targetMemberStats);

            targetMemberStats.forEach(targetStat -> {
                given(lifestyleMatchRateRepository.findById(
                    new LifestyleMatchRate.LifestyleMatchRateId(
                        memberStatA.getMember().getId(), targetStat.getMember().getId())))
                    .willReturn(Optional.empty());
            });

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            verify(lifestyleMatchRateRepository, times(targetMemberStats.size())).save(
                any(LifestyleMatchRate.class));
        }

    }

    @Nested
    @DisplayName("calculateAllLifeStyleMatchRate 테스트")
    class CalculateAllLifeStyleMatchRate {

        private University university;
        private List<MemberStat> memberStats;

        @BeforeEach
        void setUp() {
            university = UniversityFixture.createTestUniversity();
            List<Member> members = MemberFixture.리스트_커스텀(university, 5, Gender.MALE,
                LocalDate.now(), "경영학과");
            memberStats = MemberStatFixture.랜덤_멤버_스탯_리스트(members, 5, 1234L);
        }

        @Test
        @DisplayName("모든 멤버의 일치율을 계산하고 저장")
        void success_should_calculate_and_save_match_rate_for_all_university_members() {
            // given
            given(universityRepository.findAll()).willReturn(List.of(university));
            given(memberStatRepository.findByMemberUniversityAndGender(Gender.MALE,
                university.getId()))
                .willReturn(memberStats);

            // when
            lifestyleMatchRateService.calculateAllLifeStyleMatchRate();

            // then
            verify(lifestyleMatchRateRepository, atLeastOnce()).save(any(LifestyleMatchRate.class));
        }
    }

}
