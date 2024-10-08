package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
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

    private final MemberStatRepository memberStatRepository;
    private final MemberRepository memberRepository;



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

    public MemberStatQueryResponseDTO getMemberStatWithId(Long memberId) {

        Member member = memberRepository.findById(memberId). orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        Integer birthYear = member.getBirthDay().getYear();

        return MemberStatConverter.toDto(
            memberStat,birthYear
        );
    }

    public PageResponseDto<List<?>> getMemberStatList(Member member,
        List<String> filterList, Pageable pageable, boolean needsDetail) {

        //일치율의 기준이 되는 MemberStat을 가져오고, 유효성을 검사합니다.
        MemberStat criteriaMemberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        //필터링된 리스트들을 가져옵니다. 필터가 없을 경우, 모두 가져옵니다.
        List<MemberStat> filteredResult = memberStatRepository.getFilteredMemberStat(filterList,
            criteriaMemberStat);

        if (filteredResult.isEmpty()) {
            return new PageResponseDto<>(pageable.getPageNumber(), false, Collections.emptyList());
        }

        if (needsDetail) {
            List<MemberStatEqualityDetailResponseDTO> result = filteredResult.stream()
                .map(memberStat -> {
                    MemberStatEqualityResponseDTO equalityResponse = MemberStatConverter.toEqualityDto(memberStat, MemberUtil.calculateScore(
                        criteriaMemberStat, memberStat));
                    MemberStatQueryResponseDTO queryResponse = MemberStatConverter.toDto(
                        memberStat, memberStat.getMember().getBirthDay().getYear());
                    return MemberStatEqualityDetailResponseDTO.builder()
                        .info(equalityResponse)
                        .detail(queryResponse)
                        .build();
                })
                .sorted((dto1, dto2) -> Integer.compare(
                    dto2.getMemberStatEqualityResponseDTO().getEquality(),
                    dto1.getMemberStatEqualityResponseDTO().getEquality()
                ))
                .toList();
            return toPageResponseDto(result, pageable);
        }

        List<MemberStatEqualityResponseDTO> result = filteredResult
            .stream()
            .map(memberStat -> MemberStatConverter.toEqualityDto(memberStat,MemberUtil.calculateScore(criteriaMemberStat, memberStat)))
            .sorted(Comparator.comparingInt(MemberStatEqualityResponseDTO::getEquality).reversed())
            .toList();

        return toPageResponseDto(result, pageable);

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
