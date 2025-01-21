package com.cozymate.cozymate_server.domain.memberstat_v2.service;

import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.memberstat_v2.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.util.MemberStatConverter_v2;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatCommandService_v2 {

    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepository_v2 memberStatRepository;

    @Transactional
    public Long createMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        MemberStatTest memberStat = memberStatRepository.save(
            MemberStatConverter_v2.toEntity(member, createMemberStatRequestDTO));

        lifestyleMatchRateService.saveLifeStyleMatchRate(memberStat);

        return memberStat.getMember().getId();
    }

    @Transactional
    public Long modifyMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        MemberStatTest memberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));

        memberStat.update(
            MemberStatConverter_v2.toMemberUniversityStatFromDto(createMemberStatRequestDTO),
            MemberStatConverter_v2.toLifestyleFromDto(createMemberStatRequestDTO),
            createMemberStatRequestDTO.selfIntroduction()
        );

        lifestyleMatchRateService.saveLifeStyleMatchRate(memberStat);

        return memberStat.getMember().getId();
    }

}
