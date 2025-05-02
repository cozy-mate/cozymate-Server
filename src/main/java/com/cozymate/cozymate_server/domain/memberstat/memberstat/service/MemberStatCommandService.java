package com.cozymate.cozymate_server.domain.memberstat.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service.MemberStatCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatCommandService {

    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepositoryService memberStatRepositoryService;
    private final MemberStatCacheService memberStatCacheService;

    @Transactional
    public Long createMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        MemberStat memberStat = memberStatRepositoryService.createMemberStat(
            MemberStatConverter.toEntity(member, createMemberStatRequestDTO));
        memberStatCacheService.save(memberStat);

        lifestyleMatchRateService.saveLifeStyleMatchRate(memberStat);

        return memberStat.getMember().getId();
    }

    @Transactional
    public Long modifyMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        MemberStat memberStat = memberStatRepositoryService.getMemberStatOrThrow(member.getId());

        memberStatCacheService.update(
            member, memberStat,
            MemberStatConverter.toEntity(member, createMemberStatRequestDTO));

        memberStat.update(
            MemberStatConverter.toMemberUniversityStatFromDto(createMemberStatRequestDTO),
            MemberStatConverter.toLifestyleFromDto(createMemberStatRequestDTO),
            createMemberStatRequestDTO.selfIntroduction()
        );

        lifestyleMatchRateService.saveLifeStyleMatchRate(memberStat);

        return memberStat.getMember().getId();
    }

    @Transactional
    public void migrate() {
//        List<MemberStat> all = memberStatRepositoryService.getAll();
//        all.forEach(memberStatCacheService::save);
        lifestyleMatchRateService.calculateAllLifeStyleMatchRate();
    }
}
