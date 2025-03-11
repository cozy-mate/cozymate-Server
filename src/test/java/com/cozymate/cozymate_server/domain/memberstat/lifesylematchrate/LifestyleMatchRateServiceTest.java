package com.cozymate.cozymate_server.domain.memberstat.lifesylematchrate;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate.LifestyleMatchRateId;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository.LifestyleMatchRateRepositoryService;
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
    private LifestyleMatchRateRepositoryService lifestyleMatchRateRepositoryService;
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
            given(lifestyleMatchRateRepositoryService.getLifestyleMatchRateListByIdList(
                idList)).willReturn(List.of(matchRate));

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

            given(lifestyleMatchRateRepositoryService.getLifestyleMatchRateListByIdList(
                idList)).willReturn(matchRates);

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
            given(lifestyleMatchRateRepositoryService.getLifestyleMatchRateListBySingleMemberId(
                memberA.getId())).willReturn(
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

            given(lifestyleMatchRateRepositoryService.getLifestyleMatchRateListBySingleMemberId(
                memberA.getId())).willReturn(
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
            LifestyleMatchRateId lifestyleMatchRateId = new LifestyleMatchRateId(memberA.getId(),
                memberB.getId());
            LifestyleMatchRate matchRate = new LifestyleMatchRate(lifestyleMatchRateId,
                90);
            given(lifestyleMatchRateRepositoryService.getLifestyleMatchRateByIdOrNoMatchRate(
                lifestyleMatchRateId))
                .willReturn(matchRate);
            // when
            Integer result = lifestyleMatchRateService.getSingleMatchRate(memberA.getId(),
                memberB.getId());

            // then
            assertThat(result).isEqualTo(90);
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

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<List<LifestyleMatchRate>> captor = ArgumentCaptor.forClass(List.class);
            verify(lifestyleMatchRateRepositoryService, times(1))
                .createAndUpdateLifestyleMatchRateList(captor.capture());

            // 검증: 저장된 리스트가 예상대로 들어갔는지 확인
            List<LifestyleMatchRate> savedRates = captor.getValue();
            assertThat(savedRates.size()).isEqualTo(1);

            // 첫 번째 요소가 올바르게 저장되었는지 검증
            LifestyleMatchRate savedRate = savedRates.get(0);
            assertThat(savedRate.getMatchRate()).isNotNull();
            assertThat(savedRate.getId()).isNotNull();
            assertThat(savedRate.getId().getMemberA()).isEqualTo(
                Math.min(memberStatA.getMember().getId(), memberStatB.getMember().getId()));
            assertThat(savedRate.getId().getMemberB()).isEqualTo(
                Math.max(memberStatA.getMember().getId(), memberStatB.getMember().getId()));
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

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<List<LifestyleMatchRate>> captor = ArgumentCaptor.forClass(List.class);
            verify(lifestyleMatchRateRepositoryService, times(1))
                .createAndUpdateLifestyleMatchRateList(captor.capture());

            // 저장된 리스트 검증
            List<LifestyleMatchRate> savedRates = captor.getValue();
            assertThat(savedRates.size()).isEqualTo(1);

            LifestyleMatchRate savedRate = savedRates.get(0);
            assertThat(savedRate.getMatchRate()).isEqualTo(100);
            assertThat(savedRate.getId()).isNotNull();
            assertThat(savedRate.getId().getMemberA()).isEqualTo(
                Math.min(memberStatA.getMember().getId(), memberStatB.getMember().getId()));
            assertThat(savedRate.getId().getMemberB()).isEqualTo(
                Math.max(memberStatA.getMember().getId(), memberStatB.getMember().getId()));
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

            // 예상되는 일치율 (테스트 데이터를 기반으로 변경 가능)
            int expectedMatchRate = 91;

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<List<LifestyleMatchRate>> captor = ArgumentCaptor.forClass(List.class);
            verify(lifestyleMatchRateRepositoryService, times(1))
                .createAndUpdateLifestyleMatchRateList(captor.capture());

            // 저장된 리스트 검증
            List<LifestyleMatchRate> savedRates = captor.getValue();
            assertThat(savedRates.size()).isEqualTo(1);

            LifestyleMatchRate savedRate = savedRates.get(0);
            assertThat(savedRate.getMatchRate()).isEqualTo(expectedMatchRate);
            assertThat(savedRate.getId()).isNotNull();
            assertThat(savedRate.getId().getMemberA()).isEqualTo(
                Math.min(memberStatA.getMember().getId(), partialMatchedStat.getMember().getId()));
            assertThat(savedRate.getId().getMemberB()).isEqualTo(
                Math.max(memberStatA.getMember().getId(), partialMatchedStat.getMember().getId()));
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

            // when
            lifestyleMatchRateService.saveLifeStyleMatchRate(memberStatA);

            // then
            ArgumentCaptor<List<LifestyleMatchRate>> captor = ArgumentCaptor.forClass(List.class);
            verify(lifestyleMatchRateRepositoryService, times(1))
                .createAndUpdateLifestyleMatchRateList(captor.capture());

            // 검증: 저장된 리스트가 예상대로 들어갔는지 확인
            List<LifestyleMatchRate> savedRates = captor.getValue();
            assertThat(savedRates.size()).isEqualTo(targetMemberStats.size());

            // 각 저장된 요소가 예상된 값을 가지는지 확인
            for (int i = 0; i < targetMemberStats.size(); i++) {
                LifestyleMatchRate savedRate = savedRates.get(i);
                MemberStat targetStat = targetMemberStats.get(i);

                assertThat(savedRate.getMatchRate()).isNotNull();
                assertThat(savedRate.getId()).isNotNull();
                assertThat(savedRate.getId().getMemberA()).isEqualTo(
                    Math.min(memberStatA.getMember().getId(), targetStat.getMember().getId()));
                assertThat(savedRate.getId().getMemberB()).isEqualTo(
                    Math.max(memberStatA.getMember().getId(), targetStat.getMember().getId()));
            }
        }

    }

    @Nested
    @DisplayName("calculateAllLifeStyleMatchRate 테스트")
    class CalculateAllLifeStyleMatchRate {

        private University university;
        private List<MemberStat> maleMemberStats;
        private List<MemberStat> femaleMemberStats;

        @BeforeEach
        void setUp() {
            university = UniversityFixture.createTestUniversity();
            List<Member> maleMembers = MemberFixture.리스트_커스텀(university, 5, Gender.MALE,
                LocalDate.now(), "경영학과");
            List<Member> femaleMembers = MemberFixture.리스트_커스텀(university, 5, Gender.FEMALE,
                LocalDate.now(), "심리학과");

            maleMemberStats = MemberStatFixture.랜덤_멤버_스탯_리스트(maleMembers, 5, 1234L);
            femaleMemberStats = MemberStatFixture.랜덤_멤버_스탯_리스트(femaleMembers, 5, 5678L);
        }

        @Test
        @DisplayName("모든 멤버의 일치율을 계산하고 저장")
        void success_should_calculate_and_save_match_rate_for_all_university_members() {
            // given
            given(universityRepository.findAll()).willReturn(List.of(university));
            given(memberStatRepository.findByMemberUniversityAndGender(Gender.MALE,
                university.getId()))
                .willReturn(maleMemberStats);
            given(memberStatRepository.findByMemberUniversityAndGender(Gender.FEMALE,
                university.getId()))
                .willReturn(femaleMemberStats);

            // when
            lifestyleMatchRateService.calculateAllLifeStyleMatchRate();

            // then
            ArgumentCaptor<List<LifestyleMatchRate>> captor = ArgumentCaptor.forClass(List.class);
            verify(lifestyleMatchRateRepositoryService, times(2))
                .createAndUpdateLifestyleMatchRateList(captor.capture());

            // 캡처된 리스트 두 개 가져오기 (남/녀 각각)
            List<List<LifestyleMatchRate>> savedRatesList = captor.getAllValues();

            // 검증: 리스트가 두 개 존재해야 함 (남성/여성 각각)
            assertThat(savedRatesList.size()).isEqualTo(2);

            // 남성 리스트 검증
            List<LifestyleMatchRate> maleSavedRates = savedRatesList.get(0);
            assertThat(maleSavedRates).isNotNull();
            assertThat(maleSavedRates.size()).isEqualTo(maleMemberStats.size() * (maleMemberStats.size() - 1) / 2);

            // 여성 리스트 검증
            List<LifestyleMatchRate> femaleSavedRates = savedRatesList.get(1);
            assertThat(femaleSavedRates).isNotNull();
            assertThat(femaleSavedRates.size()).isEqualTo(femaleMemberStats.size() * (femaleMemberStats.size() - 1) / 2);
        }
    }


}
