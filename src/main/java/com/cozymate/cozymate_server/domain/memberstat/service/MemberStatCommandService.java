package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCommandRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityCommandService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatCommandService {

    private final MemberStatRepository memberStatRepository;
    private final UniversityRepository universityRepository;
    private final MemberStatEqualityCommandService memberStatEqualityCommandService;

    public Long createMemberStat(
        Member member, MemberStatCommandRequestDTO memberStatCommandRequestDTO) {

        // 멤버 상세정보가 이미 존재하는 경우
        if (memberStatRepository.findByMemberId(member.getId()).isPresent()) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }

        University university = universityRepository.findById(
                memberStatCommandRequestDTO.getUniversityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        MemberStat saveMemberStat = memberStatRepository.save(
            MemberStatConverter.toEntity(
                null, member, university, memberStatCommandRequestDTO
            )
        );

        memberStatEqualityCommandService.createMemberStatEqualities(
            saveMemberStat
        );

        return saveMemberStat.getId();
    }

    public Long modifyMemberStat(
        Member member, MemberStatCommandRequestDTO memberStatCommandRequestDTO) {

        University university = universityRepository.findById(
                memberStatCommandRequestDTO.getUniversityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        MemberStat updatedMemberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        updatedMemberStat.update(member, university, memberStatCommandRequestDTO);

        memberStatEqualityCommandService.updateMemberStatEqualities(
            updatedMemberStat
        );

        return updatedMemberStat.getId();

    }

    public void deleteMemberStat(Long memberId) {

        if (!memberStatRepository.existsById(memberId)) {
            throw new GeneralException(ErrorStatus._MEMBER_NOT_FOUND);
        }

        MemberStat memberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        memberStatEqualityCommandService.deleteMemberStatEqualities(
            memberStat
        );

        memberStatRepository.delete(memberStat);

    }
}
