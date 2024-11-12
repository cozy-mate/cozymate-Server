package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityCommandService;
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
    private final MemberStatEqualityCommandService memberStatEqualityCommandService;

    public Long createMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {

        if (memberStatRepository.existsByMemberId(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }

        MemberStat saveMemberStat = memberStatRepository.save(
            MemberStatConverter.toEntity(
                member, createMemberStatRequestDTO
            )
        );

        memberStatEqualityCommandService.createMemberStatEqualities(
            saveMemberStat
        );

        return saveMemberStat.getMember().getId();
    }

    public Long modifyMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {

        MemberStat updatedMemberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );

        updatedMemberStat.update(member,  createMemberStatRequestDTO);

        memberStatEqualityCommandService.updateMemberStatEqualities(
            updatedMemberStat
        );

        return updatedMemberStat.getMember().getId();

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
