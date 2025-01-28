package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.util.MemberStatConverter_v2;
import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatQueryService_v2 {

    private final MemberRepository memberRepository;
    private final MateRepository mateRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberStatRepository_v2 memberStatRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;


    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomQueryService roomQueryService;


    private static final Long NO_ROOMMATE = 0L;
    private static final Long NOT_FAVORITE = 0L;
    private static final Integer NO_EQUALITY = null;

    private static final Integer RECOMMEND_MEMBER_SIZE = 5;

    @Transactional
    public MemberStatDetailWithMemberDetailResponseDTO getMemberStat(Member member) {
        MemberStatTest memberStat = memberStatRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );
        return MemberStatConverter_v2.toMemberStatDetailWithMemberDetailDTO(memberStat);
    }

    @Transactional
    public MemberStatDetailAndRoomIdAndEqualityResponseDTO getMemberStatWithId(Member viewer,
        Long targetMemberId) {
        MemberStatTest memberStat = memberStatRepository.findByMemberId(targetMemberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        Integer matchRate = lifestyleMatchRateService.getSingleMatchRate(viewer.getId(),
            targetMemberId);

        List<Mate> mateList = mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
            targetMemberId,
            List.of(EntryStatus.PENDING, EntryStatus.JOINED),
            List.of(RoomStatus.ENABLE, RoomStatus.WAITING)
        );

        Long roomId = mateList.stream()
            .filter(mate -> mate.getEntryStatus().equals(EntryStatus.JOINED))
            .findFirst()
            .map(mate -> mate.getRoom().getId())
            .orElse(NO_ROOMMATE);

        boolean hasRequestedRoomEntry = roomId.equals(NO_ROOMMATE)
            && roomQueryService.checkInvitationStatus(viewer, mateList);

        Long favoriteId = favoriteRepository.findByMemberAndTargetIdAndFavoriteType(viewer,
                targetMemberId, FavoriteType.MEMBER)
            .map(Favorite::getId)
            .orElse(NOT_FAVORITE);

        return MemberStatConverter_v2.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
            memberStat, matchRate, roomId, hasRequestedRoomEntry, favoriteId
        );
    }

    public Integer getNumOfRoommateStatus(Long memberId) {
        return Integer.parseInt(memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        ).getMemberUniversityStat().getNumberOfRoommate());
    }

    @Transactional
    public MemberStatPageResponseDTO<List<?>> getMemberStatList(Member member,
        List<String> filterList, Pageable pageable) {

        MemberStatTest criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStatTest, Integer>> filteredResult = memberStatRepository.filterMemberStat(
            criteriaMemberStat,
            filterList,
            pageable
        );

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        return toPageResponseDto(
            createMemberStatPreferenceResponse(filteredResult, criteriaMemberStat));
    }

    public Integer getNumOfSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap) {
        MemberStatTest criteriaMemberStat = getCriteriaMemberStat(member);

        return memberStatRepository.countAdvancedFilteredMemberStat(
            criteriaMemberStat, filterMap
        );
    }

    @Transactional
    public MemberStatPageResponseDTO<List<?>> getSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap, Pageable pageable) {
        MemberStatTest criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStatTest, Integer>> filteredResult = memberStatRepository.filterMemberStatAdvance(
            criteriaMemberStat, filterMap,
            pageable);

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        return toPageResponseDto(
            createMemberStatPreferenceResponse(filteredResult, criteriaMemberStat));
    }

    @Transactional
    public MemberStatRandomListResponseDTO getRandomMemberStatWithPreferences(Member member) {
        // 상세정보가 있다면 불러올 수 없는 API
        if (existStat(member)) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }

        List<String> criteriaMemberStatPreference = memberStatPreferenceQueryService.
            getPreferencesToList(member.getId());

        List<MemberStatTest> memberStatList = memberStatRepository.
            findByMemberUniversityAndGenderWithoutSelf(
                member.getGender(),
                member.getUniversity().getId(),
                member.getId()
            );

        return MemberStatConverter_v2.toMemberStatRandomListResponseDTO(
            shuffleAndExtractMemberStatList(memberStatList),
            criteriaMemberStatPreference);
    }


    public List<MemberStatSearchResponseDTO> getMemberSearchResponse(Member searchingMember,
        String keyword) {

        if (!existStat(searchingMember)) {
            return memberRepository.findMembersWithMatchingCriteria(
                    keyword,
                    searchingMember.getUniversity().getId(),
                    searchingMember.getGender(),
                    searchingMember.getId()
                ).stream()
                .map(MemberStatConverter_v2::toMemberStatSearchResponseDTOWithOutMatchRate)
                .toList();
        }

        Map<MemberStatTest, Integer> memberStatLifestyleMatchRateMap =
            memberStatRepository.getMemberStatsWithMatchRate(
                searchingMember.getId());

        return memberStatLifestyleMatchRateMap.entrySet().stream()
            .map(entry -> {
                MemberStatTest memberStatTest = entry.getKey();
                Integer matchRate = entry.getValue();

                Member member = memberStatTest.getMember();

                return MemberStatConverter_v2.toMemberStatSearchResponseDTOWithMatchRate(member,
                    matchRate);
            })
            .toList();

    }

    private Boolean existStat(Member member) {
        return memberStatRepository.existsByMemberId(member.getId());
    }

    private Slice<MemberStatPreferenceResponseDTO> createMemberStatPreferenceResponse(
        Slice<Map<MemberStatTest, Integer>> filteredResult, MemberStatTest criteriaMemberStat) {
        return filteredResult.map(
            memberStatIntegerMap -> {
                Map.Entry<MemberStatTest, Integer> entry = memberStatIntegerMap.entrySet()
                    .iterator()
                    .next();
                MemberStatTest memberStat = entry.getKey();
                Integer equality = entry.getValue();
                List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
                    criteriaMemberStat.getMember().getId());
                return MemberStatConverter_v2.toPreferenceResponseDTO(
                    memberStat,
                    MemberStatConverter_v2.toMemberStatPreferenceDetailColorDTOList(memberStat,
                        criteriaMemberStat, criteriaPreferences),
                    equality
                );
            }
        );
    }

    private List<MemberStatTest> shuffleAndExtractMemberStatList(
        List<MemberStatTest> memberStatList) {
        Collections.shuffle(memberStatList);

        return memberStatList.subList(0,
            Math.min(RECOMMEND_MEMBER_SIZE, memberStatList.size()));
    }

    private MemberStatTest getCriteriaMemberStat(Member member) {
        return memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    private MemberStatPageResponseDTO<List<?>> createEmptyPageResponse(Pageable pageable) {
        return new MemberStatPageResponseDTO<>(pageable.getPageNumber(), false,
            Collections.emptyList());
    }

    private MemberStatPageResponseDTO<List<?>> toPageResponseDto(Slice<?> page) {
        return new MemberStatPageResponseDTO<>(
            page.getNumber(),
            page.hasNext(),
            page.getContent()
        );
    }

}


