package com.cozymate.cozymate_server.domain.memberstat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.service.MemberStatQueryService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.MemberStatFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.*;
import org.springframework.data.domain.SliceImpl;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class MemberStatQueryServiceTest {

    @Mock
    private LifestyleMatchRateService lifestyleMatchRateService;
    @Mock
    private RoomQueryService roomQueryService;
    @Mock
    private MemberStatRepository memberStatRepository;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberStatPreferenceQueryService memberStatPreferenceQueryService;
    @Mock
    private MateRepository mateRepository;
    @Mock
    private MemberFavoriteRepository memberFavoriteRepository;

    @InjectMocks
    private MemberStatQueryService memberStatQueryService;

    private Member member;
    private MemberStat memberStat;

    @Nested
    @DisplayName("getMemberStat 테스트")
    class GetMemberStat {

        @BeforeEach
        void setUp() {
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            memberStat = MemberStatFixture.정상_1(member);

            // `findByMemberId()`가 저장된 `memberStat`을 반환하도록 설정
            given(memberStatRepository.findByMemberId(member.getId()))
                .willReturn(java.util.Optional.of(memberStat));
        }

        @Test
        @DisplayName("존재하는 MemberStat 조회 성공")
        void success_when_memberStat_exists() {
            given(memberStatRepository.findByMemberId(member.getId()))
                .willReturn(Optional.of(memberStat));

            MemberStatDetailWithMemberDetailResponseDTO response
                = memberStatQueryService.getMemberStat(member);

            assertThat(response).isNotNull();
            assertThat(response.memberDetail()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 MemberStat 조회 시 예외 발생")
        void failure_when_memberStat_not_exists() {
            given(memberStatRepository.findByMemberId(member.getId()))
                .willReturn(Optional.empty());

            assertThatThrownBy(() -> memberStatQueryService.getMemberStat(member))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }
    }


    @Nested
    @DisplayName("getMemberStatWithId 테스트")
    class GetMemberStatWithIdTests {

        private Member viewer;
        private Member targetMember1;
        private MemberStat viewerMemberStat;
        private MemberStat targetMemberStat1;
        private static final Long NOT_FAVORITE = 0L;
        private static final Long NO_ROOMMATE = 0L;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();

            viewer = MemberFixture.정상_1(university);
            targetMember1 = MemberFixture.정상_2(university);

            // 실제 Lifestyle 데이터 기반 MemberStat 생성
            viewerMemberStat = MemberStatFixture.정상_1(viewer);
            targetMemberStat1 = MemberStatFixture.정상_1(targetMember1);

            // 공통적으로 사용되는 Mock 설정 (targetMember1)
            given(memberStatRepository.findByMemberId(targetMember1.getId()))
                .willReturn(Optional.of(targetMemberStat1));
        }

        @Test
        @DisplayName("MemberStat이 존재하고, 기본 데이터로 정상 조회되는 경우"
            + "일치율 계산이 제대로 됐다는 가장하에 진행합니다.")
        void success_when_memberStat_exists_and_no_room_joined() {
            // given
            given(
                lifestyleMatchRateService.getSingleMatchRate(viewer.getId(), targetMember1.getId()))
                .willReturn(85);

            given(mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
                eq(targetMember1.getId()), anyList(), anyList()))
                .willReturn(List.of());

            given(roomQueryService.checkInvitationStatus(eq(viewer), anyList()))
                .willReturn(false);

            given(
                memberFavoriteRepository.findByMemberAndTargetMember(viewer, targetMember1.getId()))
                .willReturn(Optional.empty());

            // when
            MemberStatDetailAndRoomIdAndEqualityResponseDTO response =
                memberStatQueryService.getMemberStatWithId(viewer, targetMember1.getId());

            // then
            assertThat(response.equality()).isEqualTo(85);
            assertThat(response.roomId()).isEqualTo(NO_ROOMMATE);
            assertThat(response.hasRequestedRoomEntry()).isFalse();
            assertThat(response.favoriteId()).isEqualTo(NOT_FAVORITE);
        }

        @Test
        @DisplayName("MemberStat이 존재하지 않는 경우 예외 발생")
        void failure_when_memberStat_not_exists() {
            // given
            given(memberStatRepository.findByMemberId(targetMember1.getId()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> memberStatQueryService.getMemberStatWithId(viewer, targetMember1.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }

        @Test
        @DisplayName("방에 참여한 상태인 경우 정상 반환")
        void success_when_memberStat_exists_and_room_joined() {
            // given
            Room room = RoomFixture.정상_1(targetMember1);
            Mate joinedMate = MateFixture.정상_1(room, targetMember1);
            given(mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
                eq(targetMember1.getId()), anyList(), anyList()))
                .willReturn(List.of(joinedMate));

            // when
            MemberStatDetailAndRoomIdAndEqualityResponseDTO response =
                memberStatQueryService.getMemberStatWithId(viewer, targetMember1.getId());

            // then
            assertThat(response.roomId()).isEqualTo(joinedMate.getRoom().getId());
        }

        @Test
        @DisplayName("초대 요청이 존재하는 경우 정상 반환")
        void success_when_memberStat_exists_and_has_invitation_request() {
            // given
            given(roomQueryService.checkInvitationStatus(eq(viewer), anyList()))
                .willReturn(true);

            // when
            MemberStatDetailAndRoomIdAndEqualityResponseDTO response =
                memberStatQueryService.getMemberStatWithId(viewer, targetMember1.getId());

            // then
            assertThat(response.hasRequestedRoomEntry()).isTrue();
        }
    }

    @Nested
    @DisplayName("getMemberStatList 테스트"
        + "필터링이 제대로 됐다는 가정하에 진행하기 때문에 실제 필터링 결과는 무시합니다.")
    class GetMemberStatList {

        private Member viewer;
        private MemberStat viewerMemberStat;
        private Pageable pageable;
        private List<String> filterList;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            viewer = MemberFixture.정상_1(university);
            viewerMemberStat = MemberStatFixture.정상_1(viewer);
            pageable = PageRequest.of(0, 10);
            filterList = List.of("smoking", "intimacy");

            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.of(viewerMemberStat));
        }

        @Test
        @DisplayName("필터링된 MemberStat 목록이 정상적으로 반환되는 경우")
        void success_when_filtered_memberStat_list_exists() {
            Member targetMember1 = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            Member targetMember2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            // Mock 데이터 생성
            MemberStat targetMemberStat1 = MemberStatFixture.정상_1(targetMember1);
            MemberStat targetMemberStat2 = MemberStatFixture.정상_2(targetMember2);
            Slice<Map<MemberStat, Integer>> mockSlice = new SliceImpl<>(
                List.of(
                    Map.of(targetMemberStat1, 80),
                    Map.of(targetMemberStat2, 60)
                ), pageable, false
            );

            // 필터링된 결과 Mock 설정
            given(memberStatRepository.filterMemberStat(viewerMemberStat, filterList, pageable))
                .willReturn(mockSlice);

            given(memberStatPreferenceQueryService.getPreferencesToList(viewer.getId()))
                .willReturn(List.of("smoking", "intimacy"));

            // when
            MemberStatPageResponseDTO<List<?>> response =
                memberStatQueryService.getMemberStatList(viewer, filterList, pageable);

            // then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.memberList()).hasSize(2);

            MemberStatPreferenceResponseDTO firstEntry =
                (MemberStatPreferenceResponseDTO) response.memberList().get(0);
            assertThat(firstEntry.equality()).isEqualTo(80);
            assertThat(firstEntry.memberDetail().memberId()).isEqualTo(targetMember1.getId());

            MemberStatPreferenceResponseDTO secondEntry =
                (MemberStatPreferenceResponseDTO) response.memberList().get(1);
            assertThat(secondEntry.equality()).isEqualTo(60);
            assertThat(secondEntry.memberDetail().memberId()).isEqualTo(targetMember2.getId());

        }

        @Test
        @DisplayName("필터링된 결과가 없을 경우 빈 페이지 반환")
        void success_when_no_filtered_result() {
            // 필터링된 결과가 없는 경우 Mock 설정
            Slice<Map<MemberStat, Integer>> emptySlice = new SliceImpl<>(List.of(), pageable,
                false);

            given(memberStatRepository.filterMemberStat(viewerMemberStat, filterList, pageable))
                .willReturn(emptySlice);

            // when
            MemberStatPageResponseDTO<List<?>> response =
                memberStatQueryService.getMemberStatList(viewer, filterList, pageable);

            // then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.memberList()).isEmpty();
        }

        @Test
        @DisplayName("페이징이 적용된 경우 다음 페이지 존재 여부 확인")
        void success_when_pagination_applies() {
            // 테스트용 Member 및 MemberStat 객체 생성
            Member targetMember1 = MemberFixture.정상_2(viewer.getUniversity());
            Member targetMember2 = MemberFixture.정상_3(viewer.getUniversity());

            MemberStat targetMemberStat1 = MemberStatFixture.정상_2(targetMember1);
            MemberStat targetMemberStat2 = MemberStatFixture.정상_1(targetMember2);

            // 필터링된 결과로 반환될 Slice 객체 설정 (다음 페이지 있음)
            Slice<Map<MemberStat, Integer>> mockSlice = new SliceImpl<>(
                List.of(
                    Map.of(targetMemberStat1, 90),
                    Map.of(targetMemberStat2, 70)
                ), pageable, true
            );

            // 필터링된 결과 Mock 설정
            given(memberStatRepository.filterMemberStat(viewerMemberStat, filterList, pageable))
                .willReturn(mockSlice);

            given(memberStatPreferenceQueryService.getPreferencesToList(viewer.getId()))
                .willReturn(List.of("smoking", "intimacy"));

            // when
            MemberStatPageResponseDTO<List<?>> response =
                memberStatQueryService.getMemberStatList(viewer, filterList, pageable);

            // then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.memberList()).hasSize(2);
        }

        @Test
        @DisplayName("MemberStat이 존재하지 않는 경우 예외 발생")
        void failure_when_memberStat_not_exists() {
            // MemberStat이 없는 경우로 설정
            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> memberStatQueryService.getMemberStatList(viewer, filterList, pageable))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }
    }

    @Nested
    @DisplayName("getNumOfSearchedAndFilteredMemberStatList 테스트")
    class GetNumOfSearchedAndFilteredMemberStatList {

        private Member viewer;
        private MemberStat viewerMemberStat;
        private HashMap<String, List<?>> filterMap;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            viewer = MemberFixture.정상_1(university);
            viewerMemberStat = MemberStatFixture.정상_1(viewer);

            // 필터링 조건 설정 (예시: 흡연 상태와 청결도 기준 필터링)
            filterMap = new HashMap<>();
            filterMap.put("smoking", List.of("비흡연자"));
            filterMap.put("cleanSensitivity", List.of(3));

            // MemberStat 조회 Mock 설정
            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.of(viewerMemberStat));
        }

        @Test
        @DisplayName("필터링된 MemberStat 개수가 정상적으로 반환되는 경우")
        void success_when_filtered_memberStat_count_is_returned() {
            // 필터링된 MemberStat 개수 설정 (예: 5명)
            given(memberStatRepository.countAdvancedFilteredMemberStat(viewerMemberStat, filterMap))
                .willReturn(5);

            // when
            Integer count = memberStatQueryService.getNumOfSearchedAndFilteredMemberStatList(viewer,
                filterMap);

            // then
            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("필터링된 MemberStat이 없는 경우 0 반환")
        void success_when_no_filtered_memberStat_exists() {
            // 필터링된 결과가 없을 경우
            given(memberStatRepository.countAdvancedFilteredMemberStat(viewerMemberStat, filterMap))
                .willReturn(0);

            // when
            Integer count = memberStatQueryService.getNumOfSearchedAndFilteredMemberStatList(viewer,
                filterMap);

            // then
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("조회 대상 MemberStat이 존재하지 않는 경우 예외 발생")
        void failure_when_criteria_memberStat_not_exists() {
            // MemberStat이 존재하지 않는 경우
            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> memberStatQueryService.getNumOfSearchedAndFilteredMemberStatList(viewer,
                    filterMap))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }
    }

    @Nested
    @DisplayName("getSearchedAndFilteredMemberStatList 테스트")
    class GetSearchedAndFilteredMemberStatList {

        private Member viewer;
        private MemberStat viewerMemberStat;
        private HashMap<String, List<?>> filterMap;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            viewer = MemberFixture.정상_1(university);
            viewerMemberStat = MemberStatFixture.정상_1(viewer);
            pageable = PageRequest.of(0, 10);

            // 필터링 조건 설정 (예시: 흡연 상태와 청결도 기준 필터링)
            filterMap = new HashMap<>();
            filterMap.put("smoking", List.of("비흡연자"));
            filterMap.put("cleanSensitivity", List.of(3));

            // MemberStat 조회 Mock 설정
            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.of(viewerMemberStat));
        }

        @Test
        @DisplayName("필터링된 MemberStat 목록이 정상적으로 반환되는 경우")
        void success_when_filtered_memberStat_list_exists() {
            Member targetMember1 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Member targetMember2 = MemberFixture.정상_3(UniversityFixture.createTestUniversity());

            // Mock 데이터 생성
            MemberStat targetMemberStat1 = MemberStatFixture.정상_2(targetMember1);
            MemberStat targetMemberStat2 = MemberStatFixture.정상_1(targetMember2);
            Slice<Map<MemberStat, Integer>> mockSlice = new SliceImpl<>(
                List.of(
                    Map.of(targetMemberStat1, 80),
                    Map.of(targetMemberStat2, 60)
                ), pageable, false
            );

            // 필터링된 결과 Mock 설정
            given(
                memberStatRepository.filterMemberStatAdvance(viewerMemberStat, filterMap, pageable))
                .willReturn(mockSlice);

            given(memberStatPreferenceQueryService.getPreferencesToList(viewer.getId()))
                .willReturn(List.of("smoking", "intimacy"));

            // when
            MemberStatPageResponseDTO<List<?>> response =
                memberStatQueryService.getSearchedAndFilteredMemberStatList(viewer, filterMap,
                    pageable);

            // then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.memberList()).hasSize(2);
        }

        @Test
        @DisplayName("필터링된 MemberStat이 없는 경우 빈 리스트 반환")
        void success_when_no_filtered_memberStat_exists() {
            // 필터링된 결과가 없을 경우
            Slice<Map<MemberStat, Integer>> emptySlice = new SliceImpl<>(List.of(), pageable,
                false);
            given(
                memberStatRepository.filterMemberStatAdvance(viewerMemberStat, filterMap, pageable))
                .willReturn(emptySlice);

            // when
            MemberStatPageResponseDTO<List<?>> response =
                memberStatQueryService.getSearchedAndFilteredMemberStatList(viewer, filterMap,
                    pageable);

            // then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.memberList()).isEmpty();
        }

        @Test
        @DisplayName("조회 대상 MemberStat이 존재하지 않는 경우 예외 발생")
        void failure_when_criteria_memberStat_not_exists() {
            // MemberStat이 존재하지 않는 경우
            given(memberStatRepository.findByMemberId(viewer.getId()))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(
                () -> memberStatQueryService.getSearchedAndFilteredMemberStatList(viewer, filterMap,
                    pageable))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }
    }


    @Nested
    @DisplayName("getRandomMemberStatWithPreferences 테스트")
    class GetRandomMemberStatWithPreferences {

        private Member viewer;

        @BeforeEach
        void setUp() {
            // 기본적인 Member 설정
            University university = UniversityFixture.createTestUniversity();
            viewer = MemberFixture.정상_1(university);
        }

        @Test
        @DisplayName("멤버가 MemberStat을 가지고 있지 않을 경우, 랜덤 추천 리스트가 정상 반환")
        void success_when_member_has_no_memberStat() {
            // given (테스트 내부에서 필요한 Mock만 설정)
            University university = viewer.getUniversity();
            List<MemberStat> availableMemberStats = List.of(
                MemberStatFixture.정상_1(MemberFixture.정상_2(university)),
                MemberStatFixture.정상_1(MemberFixture.정상_3(university)),
                MemberStatFixture.정상_1(MemberFixture.정상_4(university))
            );

            // 성별 & 대학 필터링 후 가져올 수 있는 MemberStat 리스트 반환
            given(memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
                viewer.getGender(),
                viewer.getUniversity().getId(),
                viewer.getId()
            )).willReturn(availableMemberStats);

            // 선호도 설정
            given(memberStatPreferenceQueryService.getPreferencesToList(viewer.getId()))
                .willReturn(List.of("smoking", "intimacy"));

            // when
            MemberStatRandomListResponseDTO response =
                memberStatQueryService.getRandomMemberStatWithPreferences(viewer);

            // then
            assertThat(response.memberList()).isNotEmpty();
            assertThat(response.memberList().size()).isLessThanOrEqualTo(5);

            // 반환된 멤버들이 예상 가능한 리스트 내에 있는지 확인
            List<Long> expectedMemberIds = availableMemberStats.stream()
                .map(stat -> stat.getMember().getId())
                .toList();

            List<Long> actualMemberIds = response.memberList().stream()
                .map(stat -> stat.memberDetail().memberId())
                .toList();

            assertThat(expectedMemberIds).containsAll(actualMemberIds);
        }

        @Test
        @DisplayName("멤버가 이미 MemberStat을 가지고 있는 경우 예외 발생")
        void failure_when_member_already_has_memberStat() {
            // given - viewer가 이미 MemberStat을 가지고 있도록 설정
            given(memberStatRepository.existsByMemberId(viewer.getId()))
                .willReturn(true);

            // when & then - 예외 발생 검증
            assertThatThrownBy(
                () -> memberStatQueryService.getRandomMemberStatWithPreferences(viewer))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MEMBERSTAT_EXISTS.getMessage());
        }

    }

    @Nested
    @DisplayName("getMemberSearchResponse 테스트")
    class GetMemberSearchResponse {

        private Member searchingMember;
        private MemberStat searchingMemberStat;
        private String keyword;

        @BeforeEach
        void setUp() {
            University university = UniversityFixture.createTestUniversity();
            searchingMember = MemberFixture.정상_1(university);
            searchingMemberStat = MemberStatFixture.정상_1(searchingMember);
            keyword = "테스트";
        }

        @Test
        @DisplayName("검색한 멤버가 MemberStat을 가지고 있지 않은 경우, 일치율 없이 결과 반환")
        void success_when_searchingMember_has_no_memberStat() {
            // given
            given(memberStatRepository.findByMemberId(searchingMember.getId()))
                .willReturn(Optional.empty());

            Member targetMember1 = MemberFixture.정상_2(searchingMember.getUniversity());
            Member targetMember2 = MemberFixture.정상_3(searchingMember.getUniversity());

            List<Member> matchedMembers = List.of(targetMember1, targetMember2);

            given(memberRepository.findMembersWithMatchingCriteria(
                keyword,
                searchingMember.getUniversity().getId(),
                searchingMember.getGender(),
                searchingMember.getId()
            )).willReturn(matchedMembers);

            // when
            List<MemberStatSearchResponseDTO> response =
                memberStatQueryService.getMemberSearchResponse(searchingMember, keyword);

            // then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).equality()).isNull(); // 일치율 없음
            assertThat(response.get(1).equality()).isNull();
        }

        @Test
        @DisplayName("검색한 멤버가 MemberStat을 가지고 있는 경우, 일치율 포함하여 결과 반환")
        void success_when_searchingMember_has_memberStat() {
            // given
            given(memberStatRepository.findByMemberId(searchingMember.getId()))
                .willReturn(Optional.of(searchingMemberStat));

            Member targetMember1 = MemberFixture.정상_2(searchingMember.getUniversity());
            Member targetMember2 = MemberFixture.정상_3(searchingMember.getUniversity());

            MemberStat targetMemberStat1 = MemberStatFixture.정상_1(targetMember1);
            MemberStat targetMemberStat2 = MemberStatFixture.정상_2(targetMember2);

            // 순서가 보장되는 LinkedHashMap 사용
            Map<MemberStat, Integer> matchRateMap = new LinkedHashMap<>();
            matchRateMap.put(targetMemberStat1, 90); // 높은 일치율
            matchRateMap.put(targetMemberStat2, 70); // 낮은 일치율

            given(memberStatRepository.getMemberStatsWithKeywordAndMatchRate(
                searchingMemberStat, keyword
            )).willReturn(matchRateMap);

            // when
            List<MemberStatSearchResponseDTO> response =
                memberStatQueryService.getMemberSearchResponse(searchingMember, keyword);

            // then
            assertThat(response).hasSize(2);

            // 정렬이 잘 되었는지 검증
            assertThat(response)
                .isSortedAccordingTo(
                    Comparator.comparing(MemberStatSearchResponseDTO::equality).reversed());

            assertThat(response.get(0).equality()).isEqualTo(90);
            assertThat(response.get(1).equality()).isEqualTo(70);
        }

    }

}
