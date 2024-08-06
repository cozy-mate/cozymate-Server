package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberUtil;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;



    public MemberStat getMemberStat(Long memberId) {

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        return memberStatRepository.findByMemberId(memberId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );
    }

    public PageResponseDto<List<MemberStatEqualityResponseDTO>> getMemberStatList(Long memberId,
        List<String> filterList, Pageable pageable) {

        //멤버의 유효성 검사
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
        //일치율의 기준이 되는 MemberStat을 가져오고, 유효성을 검사합니다.
        MemberStat criteriaMemberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        //필터링된 리스트들을 가져옵니다. 필터가 없을 경우, 모두 가져옵니다.
        List<MemberStat> filteredResult = memberStatRepository.getFilteredMemberStat(filterList,
            criteriaMemberStat);

        if (filteredResult.isEmpty()) {
            return new PageResponseDto<>(pageable.getPageNumber(), false, Collections.emptyList());
        }

        // 일치율을 계산하고, 정렬합니다.
        List<MemberStatEqualityResponseDTO> result = filteredResult
            .stream()
            .map(memberStat -> MemberUtil.toEqualityResponse(criteriaMemberStat, memberStat))
            .sorted(Comparator.comparingInt(MemberStatEqualityResponseDTO::getEquality).reversed())
            .toList();

        // MemberStat 전체 엔티티를 대상으로 하기 때문에, 우선 Page로 구현했습니다.
        // List를 Page로 변환하기 위해 아래 코드를 사용합니다.
        // 기존에 만들어진 PageResponseDTO를 활용해보려고 노력했습니다.

        // 전체 페이지 수 계산
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
