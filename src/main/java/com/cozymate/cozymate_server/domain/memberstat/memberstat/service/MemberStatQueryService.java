package com.cozymate.cozymate_server.domain.memberstat.memberstat.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.FieldInstanceResolver;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatQueryService {

    private final MemberRepository memberRepository;
    private final MateRepository mateRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberStatRepository memberStatRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;


    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomQueryService roomQueryService;


    private static final Long NO_ROOMMATE = 0L;
    private static final Long NOT_FAVORITE = 0L;
    private static final Integer NO_EQUALITY = null;

    private static final Integer RECOMMEND_MEMBER_SIZE = 5;

    @Transactional
    public MemberStatDetailWithMemberDetailResponseDTO getMemberStat(Member member) {
        MemberStat memberStat = memberStatRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );
        return MemberStatConverter.toMemberStatDetailWithMemberDetailDTO(memberStat);
    }

    @Transactional
    public MemberStatDetailAndRoomIdAndEqualityResponseDTO getMemberStatWithId(Member viewer,
        Long targetMemberId) {
        MemberStat memberStat = memberStatRepository.findByMemberId(targetMemberId).orElseThrow(
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

        return MemberStatConverter.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
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

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStat, Integer>> filteredResult = memberStatRepository.filterMemberStat(
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
        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        return memberStatRepository.countAdvancedFilteredMemberStat(
            criteriaMemberStat, filterMap
        );
    }

    @Transactional
    public MemberStatPageResponseDTO<List<?>> getSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap, Pageable pageable) {
        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStat, Integer>> filteredResult = memberStatRepository.filterMemberStatAdvance(
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

        List<MemberStat> memberStatList = memberStatRepository.
            findByMemberUniversityAndGenderWithoutSelf(
                member.getGender(),
                member.getUniversity().getId(),
                member.getId()
            );

        return MemberStatConverter.toMemberStatRandomListResponseDTO(
            shuffleAndExtractMemberStatList(memberStatList),
            criteriaMemberStatPreference);
    }


    @Transactional
    public List<MemberStatSearchResponseDTO> getMemberSearchResponse(Member searchingMember,
        String keyword) {

        Optional<MemberStat> criteriaMemberStat = memberStatRepository.findByMemberId(
            searchingMember.getId());
        if (criteriaMemberStat.isEmpty()) {
            return memberRepository.findMembersWithMatchingCriteria(
                    keyword,
                    searchingMember.getUniversity().getId(),
                    searchingMember.getGender(),
                    searchingMember.getId()
                ).stream()
                .map(MemberStatConverter::toMemberStatSearchResponseDTOWithOutMatchRate)
                .toList();
        }

        Map<MemberStat, Integer> memberStatLifestyleMatchRateMap =
            memberStatRepository.getMemberStatsWithKeywordAndMatchRate(
                criteriaMemberStat.get(), keyword);

        return memberStatLifestyleMatchRateMap.entrySet().stream()
            .map(entry -> {
                MemberStat memberStat = entry.getKey();
                Integer matchRate = entry.getValue();

                Member member = memberStat.getMember();

                return MemberStatConverter.toMemberStatSearchResponseDTOWithMatchRate(member,
                    matchRate);
            })
            .toList();

    }

    private Boolean existStat(Member member) {
        return memberStatRepository.existsByMemberId(member.getId());
    }

    private Slice<MemberStatPreferenceResponseDTO> createMemberStatPreferenceResponse(
        Slice<Map<MemberStat, Integer>> filteredResult, MemberStat criteriaMemberStat) {
        return filteredResult.map(
            memberStatIntegerMap -> {
                Map.Entry<MemberStat, Integer> entry = memberStatIntegerMap.entrySet()
                    .iterator()
                    .next();
                MemberStat memberStat = entry.getKey();
                Integer equality = entry.getValue();
                List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
                    criteriaMemberStat.getMember().getId());
                return MemberStatConverter.toPreferenceResponseDTO(
                    memberStat,
                    MemberStatConverter.toMemberStatPreferenceDetailColorDTOList(memberStat,
                        criteriaMemberStat, criteriaPreferences),
                    equality
                );
            }
        );
    }

    private List<MemberStat> shuffleAndExtractMemberStatList(
        List<MemberStat> memberStatList) {
        Collections.shuffle(memberStatList);

        return memberStatList.subList(0,
            Math.min(RECOMMEND_MEMBER_SIZE, memberStatList.size()));
    }

    private MemberStat getCriteriaMemberStat(Member member) {
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


