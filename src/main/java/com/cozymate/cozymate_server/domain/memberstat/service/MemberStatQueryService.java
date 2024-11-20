package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberStatQueryService {

    private final MemberStatRepository memberStatRepository;
    private final MemberRepository memberRepository;
    private final MateRepository mateRepository;

    private final MemberStatEqualityQueryService memberStatEqualityQueryService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;


    private static final Integer NO_EQUALITY = null;
    private static final Long NO_ROOMMATE = 0L;
    private static final Long NOT_FAVORITE = 0L;

    private final FavoriteRepository favoriteRepository;
    private final RoomQueryService roomQueryService;

    public MemberStatDetailWithMemberDetailResponseDTO getMemberStat(Member member) {

        MemberStat memberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        return MemberStatConverter.toMemberStatDetailWithMemberDetailDTO(
            memberStat
        );
    }

    public MemberStatDetailAndRoomIdAndEqualityResponseDTO getMemberStatWithId(Member viewer, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        Integer equality = memberStatEqualityQueryService.getSingleEquality(
                memberId,
                viewer.getId()
            );

        List<Mate> mateList = mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
            memberId,
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

        Long favoriteId = favoriteRepository.findByMemberAndTargetIdAndFavoriteType(viewer, memberId, FavoriteType.MEMBER)
            .map(Favorite::getId)
            .orElse(NOT_FAVORITE);

        return MemberStatConverter.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
            memberStat,
            equality,
            roomId,
            hasRequestedRoomEntry,
            favoriteId
        );
    }

    public Integer getNumOfRoommateStatus(Long memberId) {

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        return memberStat.getNumOfRoommate();
    }

    public MemberStatPageResponseDTO<List<?>> getMemberStatList(Member member,
        List<String> filterList, Pageable pageable) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getFilteredMemberStat(
            criteriaMemberStat,
            filterList,
            pageable
        );

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        return toPageResponseDto(createMemberStatPreferenceResponse(filteredResult,criteriaMemberStat.getMember()));

    }

    public MemberStatPageResponseDTO<List<?>> getSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap, Pageable pageable) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Slice<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(
            criteriaMemberStat, filterMap,
            pageable);

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        return toPageResponseDto(createMemberStatPreferenceResponse(filteredResult,criteriaMemberStat.getMember()));
    }

    public Integer getNumOfSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        return memberStatRepository.countAdvancedFilteredMemberStat(
            criteriaMemberStat,filterMap
        );

    }

    public MemberStatRandomListResponseDTO getRandomMemberStatWithPreferences(Member member) {

        // 상세정보가 있다면 불러올 수 없는 API
        if(memberStatRepository.existsByMemberId(member.getId())){
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }
        // 기준 멤버의 선호도 필드 목록 가져오기
        List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());
        // 같은 성별과 대학의 멤버 중, 자신과 이미 본 멤버를 제외
        List<MemberStat> memberStatList = memberStatRepository.findByMember_GenderAndMember_University_Id(
                member.getGender(), member.getUniversity().getId()
            ).stream()
            .filter(stat -> !stat.getMember().getId().equals(member.getId())) // 자신 제외
            .collect(Collectors.toList());

        // 리스트를 랜덤하게 섞고, 최대 5개의 멤버를 선택
        Collections.shuffle(memberStatList);
        List<MemberStat> randomMemberStats = memberStatList.stream()
            .limit(5)
            .toList();

        // 선택된 멤버들에 대해 MemberStatPreferenceResponseDTO 리스트 생성
        List<MemberStatPreferenceResponseDTO> preferenceResponseList = randomMemberStats.stream()
            .map(stat -> {
                Map<String, Object> preferences = MemberStatUtil.getMemberStatFields(stat,
                    criteriaPreferences);
                return MemberStatConverter.toPreferenceResponseDTO(
                    stat,
                    preferences,
                    NO_EQUALITY
                );
            })
            .toList();

        return MemberStatConverter.toMemberStatRandomListDTO(
            preferenceResponseList);

    }

    public Slice<MemberStatPreferenceResponseDTO> createMemberStatPreferenceResponse(
        Slice<Map<MemberStat, Integer>> filteredResult, Member criteriaMember) {

        return filteredResult.map(
            memberStatIntegerMap ->{
                Map.Entry<MemberStat, Integer> entry = memberStatIntegerMap.entrySet().iterator()
                    .next();
                MemberStat memberStat = entry.getKey();
                Integer equality = entry.getValue();
                List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
                    criteriaMember.getId());
                Map<String,Object> preferences = MemberStatUtil.getMemberStatFields(memberStat,
                    criteriaPreferences);
                return MemberStatConverter.toPreferenceResponseDTO(
                    memberStat,
                    preferences,
                    equality
                );
            }
        );
    }

    public List<MemberStatSearchResponseDTO> getMemberSearchResponse(String subString, Member searchingMember) {

        // 가독성을 위해 분리해 봄
        Long universityId = searchingMember.getUniversity().getId();
        Gender gender = searchingMember.getGender();
        Long searchingMemberId = searchingMember.getId();

        //memberStat이 존재할 때
        if(memberStatRepository.existsByMemberId(searchingMemberId)){
            List<Member> memberList = memberRepository.findMembersWithMatchingCriteria(
                subString,
                universityId,
                gender,
                searchingMember.getMemberStat().getNumOfRoommate(),
                searchingMember.getMemberStat().getDormitoryName(),
                searchingMemberId
            );
            return memberList.stream()
                .map(member -> {
                    Integer equality = memberStatEqualityQueryService.getSingleEquality(searchingMember.getId(), member.getId());
                    return MemberStatConverter.toMemberStatSearchResponseDTO(member, equality);
                })
                .sorted(Comparator.comparing(MemberStatSearchResponseDTO::equality).reversed()) // equality 기준으로 내림차순 정렬
                .toList();
        }

        // memberStat이 존재하지 않을 때
        List<Member> memberList = memberRepository.findMembersWithMatchingCriteria(
            subString, universityId, gender,searchingMemberId
        );
        return memberList.stream()
            .map(member-> MemberStatConverter.toMemberStatSearchResponseDTO(member, NO_EQUALITY))
            .toList();
    }

    private MemberStat getCriteriaMemberStat(Member member) {
        return memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    private MemberStatPageResponseDTO<List<?>> createEmptyPageResponse(Pageable pageable) {
        return new MemberStatPageResponseDTO<>(pageable.getPageNumber(), false, Collections.emptyList());
    }

    private MemberStatPageResponseDTO<List<?>> toPageResponseDto(Slice<?> page) {
        return new MemberStatPageResponseDTO<>(
            page.getNumber(),
            page.hasNext(),
            page.getContent()
        );
    }
}
