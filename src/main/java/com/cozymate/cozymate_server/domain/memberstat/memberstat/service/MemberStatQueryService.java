package com.cozymate.cozymate_server.domain.memberstat.memberstat.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;

import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
    private final MemberFavoriteRepository memberFavoriteRepository;
    private final MemberStatRepositoryService memberStatRepositoryService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;


    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomQueryService roomQueryService;
    private final RoomRepositoryService roomRepositoryService;


    private static final Long NO_ROOMMATE = 0L;
    private static final Long NOT_FAVORITE = 0L;

    private static final Integer RECOMMEND_MEMBER_SIZE = 5;

    /**
     * 사용자의 MemberStat 정보를 조회하여 상세 정보를 반환합니다.
     *
     * @param member 조회할 사용자의 정보
     * @return MemberStat과 MemberDetail 정보를 포함한 DTO
     * @throws GeneralException MEMBERSTAT_NOT_EXISTS 예외 발생 가능
     */
    @Transactional(readOnly = true)
    public MemberStatDetailWithMemberDetailResponseDTO getMemberStat(Member member) {
        MemberStat memberStat = memberStatRepositoryService.getMemberStatOrThrow(member.getId());
        return MemberStatConverter.toMemberStatDetailWithMemberDetailDTO(memberStat);
    }

    /**
     * 특정 사용자의 MemberStat을 조회하고, 해당 사용자의 룸메이트 및 선호도 정보를 반환합니다.
     *
     * @param viewer         조회하는 사용자
     * @param targetMemberId 조회 대상 사용자의 ID
     * @return MemberStat과 룸메이트 정보, 선호 여부 등을 포함한 DTO
     * @throws GeneralException MEMBERSTAT_NOT_EXISTS 예외 발생 가능
     *                          <p>
     *                          todo: 개선 필요 쿼리가 너무 여러개임
     */
    @Transactional(readOnly = true)
    public MemberStatDetailAndRoomIdAndEqualityResponseDTO getMemberStatWithId(Member viewer,
        Long targetMemberId) {
        MemberStat memberStat = memberStatRepositoryService.getMemberStatOrThrow(targetMemberId);

        Integer matchRate = lifestyleMatchRateService.getSingleMatchRate(viewer.getId(),
            targetMemberId);

        List<Mate> mateList = getMateListByMemberIdAndEntryStatusAndRoomStatus(targetMemberId);

        Long roomId = getRoomIdByMateList(mateList);

        boolean isRoomPublic = isRoomPublic(roomId);

        boolean hasRequestedRoomEntry = hasRequestedRoomEntry(roomId, viewer, mateList);

        Long favoriteId = getFavoriteId(viewer, targetMemberId);

        return MemberStatConverter.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
            memberStat, matchRate, roomId, isRoomPublic, hasRequestedRoomEntry, favoriteId
        );
    }


    /**
     * 사용자의 인실(Roommate 수) 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 ID
     * @return 사용자의 인실(Roommate 수), 값이 없는 경우는0 반환
     */
    public String getNumOfRoommateStatus(Long memberId) {
        return Optional.ofNullable(memberStatRepositoryService.getMemberStatOrThrow(memberId)
                .getMemberUniversityStat().getNumberOfRoommate())
            .filter(str -> !str.isEmpty())
            .orElse("0");
    }


    /**
     * 필터링된 MemberStat 목록을 조회합니다.
     *
     * @param member     조회하는 사용자
     * @param filterList 적용할 필터 리스트
     * @param pageable   페이징 정보
     * @return 필터링된 MemberStat 목록
     */
    @Transactional(readOnly = true)
    public MemberStatPageResponseDTO<List<?>> getMemberStatList(Member member,
        List<String> filterList, Pageable pageable) {

        MemberStat criteriaMemberStat = memberStatRepositoryService.getMemberStatOrThrow(
            member.getId());

        Slice<Map<MemberStat, Integer>> filteredResult =
            memberStatRepositoryService.getMemberStatListByAttributeList(
                criteriaMemberStat,
                filterList,
                pageable
            );
        return getPageResponseOrEmpty(
            filteredResult,
            pageable,
            slice -> createMemberStatPreferenceResponse(slice, criteriaMemberStat)
        );

    }

    /**
     * 필터링된 MemberStat 개수를 반환합니다.
     *
     * @param member    조회하는 사용자
     * @param filterMap 필터 조건을 포함한 맵
     * @return 필터링된 MemberStat 개수
     */
    @Transactional(readOnly = true)
    public Integer getNumberOfSearchedAndFilteredMemberStatList(Member member,
        Map<String, List<?>> filterMap) {
        MemberStat criteriaMemberStat = memberStatRepositoryService.getMemberStatOrThrow(
            member.getId());

        Map<String, List<?>> convertedMapFilter = QuestionAnswerMapper.convertFilterMap(filterMap);

        return memberStatRepositoryService.getNumberOfMemberStatByAttributeAndValuesMap(
            criteriaMemberStat, convertedMapFilter
        );
    }

    /**
     * 필터링된 MemberStat 목록을 조회합니다.
     *
     * @param member    조회하는 사용자
     * @param filterMap 필터 조건을 포함한 맵
     * @param pageable  페이징 정보
     * @return 필터링된 MemberStat 목록
     */
    @Transactional(readOnly = true)
    public MemberStatPageResponseDTO<List<?>> getSearchedAndFilteredMemberStatList(Member member,
        Map<String, List<?>> filterMap, Pageable pageable) {

        MemberStat criteriaMemberStat = memberStatRepositoryService.getMemberStatOrThrow(
            member.getId());

        Map<String, List<?>> convertedMapFilter = QuestionAnswerMapper.convertFilterMap(filterMap);
        Slice<Map<MemberStat, Integer>> filteredResult =
            memberStatRepositoryService.getMemberStatListByAttributeAndValuesMap(
                criteriaMemberStat,
                convertedMapFilter,
                pageable);

        return getPageResponseOrEmpty(
            filteredResult,
            pageable,
            slice -> createMemberStatPreferenceResponse(slice, criteriaMemberStat)
        );
    }

    /**
     * 랜덤한 MemberStat 추천 목록을 조회합니다.
     *
     * @param member 조회하는 사용자
     * @return 추천 MemberStat 목록
     * @throws GeneralException MEMBERSTAT_EXISTS 예외 발생 가능
     */
    @Transactional(readOnly = true)
    public MemberStatRandomListResponseDTO getRandomMemberStatWithPreferences(Member member) {
        // 상세정보가 있다면 불러올 수 없는 API
        if (memberStatRepositoryService.existsMemberStat(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }

        List<String> criteriaMemberStatPreference = memberStatPreferenceQueryService.
            getPreferencesToList(member.getId());

        List<MemberStat> memberStatList = memberStatRepositoryService.
            getMemberStatListByUniversityAndGenderWithoutSelf(
                member.getGender(),
                member.getUniversity().getId(),
                member.getId()
            );

        return MemberStatConverter.toMemberStatRandomListResponseDTO(
            shuffleAndExtractMemberStatList(memberStatList),
            criteriaMemberStatPreference);
    }

    /**
     * 검색어를 기반으로 MemberStat을 찾습니다. 라이프스타일이 있냐 없냐에 따라 응답이 달라집니다.
     *
     * @param viewer  검색하는 사용자
     * @param keyword 검색 키워드
     * @return 검색된 MemberStat 목록
     */
    @Transactional(readOnly = true)
    public List<MemberStatSearchResponseDTO> getMemberSearchResponse(Member viewer,
        String keyword) {
        return memberStatRepositoryService.getMemberStatOptional(viewer.getId())
            .map(criteriaStat -> memberStatRepositoryService.getMemberStatByKeywordWithMatchRate(
                    criteriaStat, keyword)
                .entrySet().stream()
                .map(entry -> MemberStatConverter.toMemberStatSearchResponseDTOWithMatchRate(
                    entry.getKey().getMember(), entry.getValue()))
                .toList()
            )
            .orElse(memberRepository.findMembersWithMatchingCriteria(keyword,
                    viewer.getUniversity().getId(),
                    viewer.getGender(),
                    viewer.getId())
                .stream()
                .map(MemberStatConverter::toMemberStatSearchResponseDTOWithOutMatchRate)
                .toList()
            );

    }

    private Long getFavoriteId(Member member, Long targetMemberId) {
        return memberFavoriteRepository.findByMemberAndTargetMember(member,
                targetMemberId)
            .map(MemberFavorite::getId)
            .orElse(NOT_FAVORITE);
    }

    private List<Mate> getMateListByMemberIdAndEntryStatusAndRoomStatus(Long memberId) {
        return mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
            memberId,
            List.of(EntryStatus.PENDING, EntryStatus.JOINED),
            List.of(RoomStatus.ENABLE, RoomStatus.WAITING)
        );

    }

    private boolean hasRequestedRoomEntry(Long roomId, Member member, List<Mate> mateList) {
        return roomId.equals(NO_ROOMMATE)
            && roomQueryService.checkInvitationStatus(member, mateList);
    }

    private Slice<MemberStatPreferenceResponseDTO> createMemberStatPreferenceResponse(
        Slice<Map<MemberStat, Integer>> filteredResult, MemberStat criteriaMemberStat) {
        return MemberStatConverter.toMemberStatPreferenceResponse(filteredResult,
            criteriaMemberStat, memberStatPreferenceQueryService.getPreferencesToList(
                criteriaMemberStat.getMember().getId()));
    }

    private List<MemberStat> shuffleAndExtractMemberStatList(
        List<MemberStat> memberStatList) {
        List<MemberStat> mutableList = new ArrayList<>(memberStatList);
        Collections.shuffle(mutableList);

        return mutableList.subList(0,
            Math.min(RECOMMEND_MEMBER_SIZE, mutableList.size()));
    }

    private MemberStatPageResponseDTO<List<?>> toPageResponseDto(Slice<?> page) {
        return new MemberStatPageResponseDTO<>(
            page.getNumber(),
            page.hasNext(),
            page.getContent()
        );
    }

    private Long getRoomIdByMateList(List<Mate> mateList) {
        return mateList.stream()
            .filter(mate -> mate.getEntryStatus().equals(EntryStatus.JOINED))
            .findFirst()
            .map(mate -> mate.getRoom().getId())
            .orElse(NO_ROOMMATE);
    }

    private <T> MemberStatPageResponseDTO<List<?>> getPageResponseOrEmpty(
        Slice<Map<MemberStat, Integer>> filteredResult,
        Pageable pageable,
        Function<Slice<Map<MemberStat, Integer>>, Slice<T>> converter
    ) {
        if (filteredResult.isEmpty()) {
            return new MemberStatPageResponseDTO<>(pageable.getPageNumber(), false,
                Collections.emptyList());
        }
        return toPageResponseDto(converter.apply(filteredResult));
    }

    private Boolean isRoomPublic(Long roomId) {
        return roomRepositoryService.getRoomOptional(roomId)
            .map(value -> value.getRoomType().equals(RoomType.PUBLIC)).orElse(false);
    }
}


