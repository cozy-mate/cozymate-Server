package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailWithMemberDetailDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatRandomListDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private static final Integer EQUALITY_ZERO = 0;

    public MemberStatDetailWithMemberDetailDTO getMemberStat(Member member) {

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

        // 본인은 일치율 0으로 처리
        Integer equality = EQUALITY_ZERO;

        if(!viewer.getId().equals(memberId)){
            equality = memberStatEqualityQueryService.getSingleEquality(
                memberId,
                viewer.getId()
            );
        }

        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));

        return MemberStatConverter.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
            memberStat,
            equality,
            mate.isPresent() ? mate.get().getRoom().getId() : 0
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

        Page<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getFilteredMemberStat(
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

        Page<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(
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

        Map<Member, MemberStat> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(
            filterMap,
            criteriaMemberStat);

        return filteredResult.size();

    }

    public MemberStatRandomListDTO getRandomMemberStatWithPreferences(Member member) {

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
                    0
                );
            })
            .toList();

        return MemberStatConverter.toMemberStatRandomListDTO(
            preferenceResponseList);

    }

    public Page<MemberStatPreferenceResponseDTO> createMemberStatPreferenceResponse(
        Page<Map<MemberStat, Integer>> filteredResult, Member criteriaMember) {

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
        Integer numOfRoomMateOfSearchingMember = searchingMember.getMemberStat().getNumOfRoommate();
        Long universityId = searchingMember.getUniversity().getId();
        Gender gender = searchingMember.getGender();
        String dormitoryName = searchingMember.getMemberStat().getDormitoryName();
        Long searchingMemberId = searchingMember.getId();

        //memberStat이 존재할 때
        if(memberStatRepository.existsByMemberId(searchingMemberId)){
            List<Member> memberList = memberRepository.findMembersWithMatchingCriteria(
                subString, universityId, gender, numOfRoomMateOfSearchingMember, dormitoryName, searchingMemberId
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
            .map(member-> MemberStatConverter.toMemberStatSearchResponseDTO(member, 0))
            .toList();
    }

    private MemberStat getCriteriaMemberStat(Member member) {
        return memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    private MemberStatPageResponseDTO<List<?>> createEmptyPageResponse(Pageable pageable) {
        return new MemberStatPageResponseDTO<>(pageable.getPageNumber(), false, Collections.emptyList());
    }

    private MemberStatPageResponseDTO<List<?>> toPageResponseDto(Page<?> page) {
        return new MemberStatPageResponseDTO<>(
            page.getNumber(),
            page.hasNext(),
            page.getContent()
        );
    }
}
