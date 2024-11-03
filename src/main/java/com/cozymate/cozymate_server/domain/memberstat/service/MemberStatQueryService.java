package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private static final Long ROOM_NOT_EXISTS = 0L;

    public MemberStatQueryResponseDTO getMemberStat(Member member) {

        Integer birthYear = member.getBirthDay().getYear();

        MemberStat memberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        return MemberStatConverter.toDto(
            memberStat, birthYear
        );
    }

    public MemberStatDetailResponseDTO getMemberStatWithId(Member viewer, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        Integer birthYear = member.getBirthDay().getYear();

        Integer equality = memberStatEqualityQueryService.getSingleEquality(
            memberId,
            viewer.getId()
        );

        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));

        return MemberStatConverter.toDetailDto(
            memberStat,
            birthYear,
            equality,
            mate.isPresent() ?
                mate.get().getRoom().getId()
                : ROOM_NOT_EXISTS
        );
    }

    public Integer getNumOfRoommateStatus(Long memberId) {

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        return memberStat.getNumOfRoommate();
    }

    public PageResponseDto<List<?>> getMemberStatList(Member member,
        List<String> filterList, Pageable pageable, boolean needsDetail) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        // List<MemberStat> -> Map<Member,MemberStat>으로 변경,
        // LazyFetch로 인해서 N+1 문제 발생해, 쿼리를 한번에 처리하기로 결정.
//        Map<Member, MemberStat> filteredResult = memberStatRepository.getFilteredMemberStat(filterList,
//            criteriaMemberStat);

        Page<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getFilteredMemberStat(
            criteriaMemberStat,
            filterList,
            pageable
        );

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        //N+1 문제 발생, 해결 위해 MAP을 활용한 방식 적용
        //Filtering 시 Member와 MemberStat을 join을 하기 때문에, Member를 Select하는 것을 추가한다고
        // 자원 소모의차이가 크지 않을 것이라고 생각했음.

//        Map<Long,Integer> memberStatEqualities = memberStatEqualityQueryService.getEquality(
//            criteriaMemberStat.getMember().getId(),
//            filteredResult.stream().map(memberStat -> memberStat.getMember().getId()).toList()
//        );

        // 이 부분 이후는 DB 안 건들고, Application 내에서 계산.
        // 쿼리는 필터링은 한번,
        // 일치율 조회 한번으로 줄이고(N+1 Problem -> 조회 1회), 응답속도를 비약적으로 개선함.
        if (needsDetail) {
            return toPageResponseDto(createDetailedResponse(filteredResult));
        }

        return toPageResponseDto(createEqualityResponse(filteredResult));


    }

    public PageResponseDto<List<?>> getSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap, Pageable pageable, boolean needsDetail) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Page<Map<MemberStat, Integer>> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(
            criteriaMemberStat, filterMap,
            pageable);

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        if (needsDetail) {
            return toPageResponseDto(createDetailedResponse(filteredResult));
        }

        return toPageResponseDto(createEqualityResponse(filteredResult));

    }

    public Integer getNumOfSearchedAndFilteredMemberStatList(Member member,
        HashMap<String, List<?>> filterMap) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Map<Member, MemberStat> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(
            filterMap,
            criteriaMemberStat);

        return filteredResult.size();

    }

    private Page<MemberStatEqualityDetailResponseDTO> createDetailedResponse(
        Page<Map<MemberStat, Integer>> filteredResult) {
        return filteredResult.map(
            memberStatIntegerMap -> {
                Map.Entry<MemberStat, Integer> entry = memberStatIntegerMap.entrySet().iterator()
                    .next();
                MemberStat memberStat = entry.getKey();
                Integer equality = entry.getValue();
                return MemberStatConverter.toEqualityDetailDto(
                    memberStat,
                    equality);
            }
        );
    }

    private Page<MemberStatEqualityResponseDTO> createEqualityResponse(
        Page<Map<MemberStat, Integer>> filteredResult) {

        return filteredResult.map(
            memberStatIntegerMap -> {
                Map.Entry<MemberStat, Integer> entry = memberStatIntegerMap.entrySet().iterator()
                    .next();
                MemberStat memberStat = entry.getKey();
                Integer equality = entry.getValue();
                return MemberStatConverter.toEqualityDto(memberStat, equality);
            }
        );
    }

    private MemberStat getCriteriaMemberStat(Member member) {
        return memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    private PageResponseDto<List<?>> createEmptyPageResponse(Pageable pageable) {
        return new PageResponseDto<>(pageable.getPageNumber(), false, Collections.emptyList());
    }

    private PageResponseDto<List<?>> toPageResponseDto(Page<?> page) {
        return new PageResponseDto<>(
            page.getNumber(),
            page.hasNext(),
            page.getContent()
        );
    }
}
