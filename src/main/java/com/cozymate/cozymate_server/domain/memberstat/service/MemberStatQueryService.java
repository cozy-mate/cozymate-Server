package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatSeenListDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceCommandService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    private final MemberStatEqualityQueryService memberStatEqualityQueryService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;
    private final MateRepository mateRepository;

    private static final Long ROOM_NOT_EXISTS = 0L;
    private final MemberStatPreferenceCommandService memberStatPreferenceCommandService;

    public MemberStatQueryResponseDTO getMemberStat(Member member) {

        Integer birthYear = member.getBirthDay().getYear();

        MemberStat memberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        return MemberStatConverter.toDto(
            memberStat,birthYear
        );
    }

    public MemberStatDetailResponseDTO getMemberStatWithId(Member viewer, Long memberId) {

        Member member = memberRepository.findById(memberId). orElseThrow(
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
        Map<Member, MemberStat> filteredResult = memberStatRepository.getFilteredMemberStat(filterList,
            criteriaMemberStat);

        if (filteredResult.isEmpty()){
            return createEmptyPageResponse(pageable);
        }

        //N+1 문제 발생, 해결 위해 MAP을 활용한 방식 적용
        //Filtering 시 Member와 MemberStat을 join을 하기 때문에, Member를 Select하는 것을 추가한다고
        // 자원 소모의차이가 크지 않을 것이라고 생각했음.

//        Map<Long,Integer> memberStatEqualities = memberStatEqualityQueryService.getEquality(
//            criteriaMemberStat.getMember().getId(),
//            filteredResult.stream().map(memberStat -> memberStat.getMember().getId()).toList()
//        );


        List<Long> memberIds = filteredResult.keySet().stream()
            .map(Member::getId)
            .toList();


        Map<Long,Integer> memberStatEqualities = memberStatEqualityQueryService.getEquality(member.getId(), memberIds);

        // 이 부분 이후는 DB 안 건들고, Application 내에서 계산.
        // 쿼리는 필터링은 한번,
        // 일치율 조회 한번으로 줄이고(N+1 Problem -> 조회 1회), 응답속도를 비약적으로 개선함.
        if (needsDetail) {
            List<MemberStatEqualityDetailResponseDTO> result =
                createDetailedResponse(filteredResult, memberStatEqualities);
            return toPageResponseDto(result, pageable);
        }

        List<MemberStatEqualityResponseDTO> result =
            createEqualityResponse(filteredResult, memberStatEqualities);
        return toPageResponseDto(result, pageable);

    }

    public PageResponseDto<List<?>> getSearchedAndFilteredMemberStatList(Member member, HashMap<String, List<?>> filterMap, Pageable pageable, boolean needsDetail) {

        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Map<Member,MemberStat> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(filterMap,
            criteriaMemberStat);

        if (filteredResult.isEmpty()) {
            return createEmptyPageResponse(pageable);
        }

        List<Long> memberIds = filteredResult.keySet().stream()
            .map(Member::getId)
            .toList();

        Map<Long,Integer> memberStatEqualities = memberStatEqualityQueryService.getEquality(member.getId(), memberIds);

        if (needsDetail) {
            List<MemberStatEqualityDetailResponseDTO> result =
                createDetailedResponse(filteredResult, memberStatEqualities);
            return toPageResponseDto(result, pageable);
        }

        List<MemberStatEqualityResponseDTO> result =
            createEqualityResponse(filteredResult, memberStatEqualities);
        return toPageResponseDto(result, pageable);

    }

    public Integer getNumOfSearchedAndFilteredMemberStatList(Member member, HashMap<String, List<?>> filterMap) {
        // 여기서 드는 의문.. 쿼리 개수 vs 쿼리 무게
        MemberStat criteriaMemberStat = getCriteriaMemberStat(member);

        Map<Member,MemberStat> filteredResult = memberStatRepository.getAdvancedFilteredMemberStat(filterMap,
            criteriaMemberStat);

        return filteredResult.size();


    }

    public MemberStatRandomListResponseDTO getRandomMemberStatWithPreferences(Member member, MemberStatSeenListDTO currentMemberStatIds) {

        // 기준 멤버의 선호도 필드 목록 가져오기
        List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(member.getId());

        List<Long> seenMemberStatIds = currentMemberStatIds.getSeenMemberStatIds();
        // 같은 성별과 대학의 멤버 중, 자신과 이미 본 멤버를 제외
        List<MemberStat> memberStatList = memberStatRepository.findByMember_GenderAndMember_University_Id(
                member.getGender(), member.getUniversity().getId()
            ).stream()
            .filter(stat -> !stat.getMember().getId().equals(member.getId())) // 자신 제외
            .filter(stat -> !seenMemberStatIds.contains(stat.getId())) // 이미 본 멤버 제외
            .collect(Collectors.toList());

        // 리스트를 랜덤하게 섞고, 최대 5개의 멤버를 선택
        Collections.shuffle(memberStatList);
        List<MemberStat> randomMemberStats = memberStatList.stream()
            .limit(5)
            .toList();

        // 선택된 멤버들에 대해 MemberStatPreferenceResponseDTO 리스트 생성
        List<MemberStatPreferenceResponseDTO> preferenceResponseList = randomMemberStats.stream()
            .map(stat -> {
                Map<String, Object> preferences = MemberStatUtil.getMemberStatFields(stat, criteriaPreferences);
                MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO = MemberStatPreferenceResponseDTO.builder()
                    .memberId(stat.getMember().getId())
                    .memberNickName(stat.getMember().getNickname())
                    .preferenceStats(preferences)
                    .build();

                // 누적 ID 리스트에 새로 선택된 멤버 ID 추가
                seenMemberStatIds.add(stat.getId());

                return memberStatPreferenceResponseDTO;
            })
            .collect(Collectors.toList());

        // MemberStatRandomListResponseDTO 생성하여 반환
        return MemberStatRandomListResponseDTO.builder()
            .memberList(preferenceResponseList) // 리스트 형식으로 추가
            .seenMemberStatIds(new ArrayList<>(seenMemberStatIds)) // 누적 리스트 포함
            .build();
    }




    private List<MemberStatEqualityDetailResponseDTO> createDetailedResponse(
        Map<Member, MemberStat> memberStats, Map<Long, Integer> memberStatEqualities) {

        List<MemberStatEqualityResponseDTO> memberStatEqualityResponseDTOList = createEqualityResponse(memberStats,memberStatEqualities);

        return memberStatEqualityResponseDTOList.stream()
            .map(equalityResponse -> {

                MemberStat memberStat = memberStats.values().stream()
                    .filter(stat -> stat.getMember().getId().equals(equalityResponse.getMemberId()))
                    .findFirst()
                    .orElse(null);

                MemberStatQueryResponseDTO queryResponse = MemberStatConverter.toDto(
                    memberStat, memberStat.getMember().getBirthDay().getYear()
                );

                return MemberStatEqualityDetailResponseDTO.builder()
                    .info(equalityResponse)
                    .detail(queryResponse)
                    .build();
            })
            .toList();
    }

    private List<MemberStatEqualityResponseDTO> createEqualityResponse(
        Map<Member, MemberStat> memberStats, Map<Long, Integer> memberStatEqualities) {

        return memberStats.entrySet().stream()
            .map(entry -> {
                Member member = entry.getKey();
                MemberStat memberStat = entry.getValue();

                return MemberStatConverter.toEqualityDto(
                    memberStat,
                    memberStatEqualities.getOrDefault(member.getId(), 0)
                );
            })
            .sorted(Comparator.comparingInt(MemberStatEqualityResponseDTO::getEquality).reversed())
            .toList();
    }

    private MemberStat getCriteriaMemberStat(Member member) {
        return memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    private PageResponseDto<List<?>> createEmptyPageResponse(Pageable pageable) {
        return new PageResponseDto<>(pageable.getPageNumber(), false, Collections.emptyList());
    }

    private PageResponseDto<List<?>> toPageResponseDto(List<?> result, Pageable pageable) {

        int totalPages = (int) Math.ceil((double) result.size() / pageable.getPageSize());

        // 요청한 페이지가 범위를 벗어났을 경우 빈 페이지 반환
        if (pageable.getPageNumber() >= totalPages || pageable.getPageNumber() < 0) {
            return new PageResponseDto<>(pageable.getPageNumber(), false, Collections.emptyList());
        }

        // 요청한 페이지의 시작과 끝 인덱스 계산
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());

        // 해당 페이지의 데이터만 추출하여 Page로 반환
        return new PageResponseDto<>(pageable.getPageNumber(),
            pageable.getPageNumber() + 1 < totalPages, result.subList(start, end));

    }
}
