package com.cozymate.cozymate_server.domain.memberstat.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.event.MemberStatCreatedEvent;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.event.MemberStatModifiedEvent;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.SaveCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service.MemberStatCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util.MemberStatExtractor;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatCommandService {

    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepositoryService memberStatRepositoryService;
    private final MemberStatCacheService memberStatCacheService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Long createMemberStat(Member member, CreateMemberStatRequestDTO req) {
        MemberStat entity = memberStatRepositoryService.createMemberStat(
            MemberStatConverter.toEntity(member, req));

        Long memberId = entity.getMember().getId();
        Long universityId = entity.getMember().getUniversity().getId();
        String gender = entity.getMember().getGender().toString();
        Map<String, String> answers = MemberStatExtractor.extractAnswers(entity);

        publisher.publishEvent(new MemberStatCreatedEvent(memberId, universityId, gender, answers));
        return memberId;
    }

    @Transactional
    public Long modifyMemberStat(Member member, CreateMemberStatRequestDTO req) {
        MemberStat entity = memberStatRepositoryService.getMemberStatOrThrow(member.getId());

        Map<String, String> oldAnswers = MemberStatExtractor.extractAnswers(entity);
        MemberStat newSnapshot = MemberStatConverter.toEntity(member, req);
        Map<String, String> newAnswers = MemberStatExtractor.extractAnswers(newSnapshot);

        entity.update(
            MemberStatConverter.toMemberUniversityStatFromDto(req),
            MemberStatConverter.toLifestyleFromDto(req),
            req.selfIntroduction()
        );

        Long memberId = member.getId();
        Long universityId = member.getUniversity().getId();
        String gender = member.getGender().toString();

        publisher.publishEvent(new MemberStatModifiedEvent(
            memberId, universityId, gender, oldAnswers, newAnswers
        ));
        return memberId;
    }

    @Transactional
    public void migrate() {
        List<MemberStat> all = memberStatRepositoryService.getAll();
        all.forEach(
            memberStat -> memberStatCacheService.saveByArgs(
                new SaveCommand(
                    memberStat.getMember().getUniversity().getId(),
                    memberStat.getMember().getGender().toString(),
                    memberStat.getMember().getId().toString(),
                    MemberStatExtractor.extractAnswers(memberStat)
                )
        ));
        lifestyleMatchRateService.calculateAllLifeStyleMatchRate();
    }
}
