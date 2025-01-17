package com.cozymate.cozymate_server.domain.memberstat_v2.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.util.MemberStatConverter_v2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatService_v2 {

    private final MemberStatRepository_v2 memberStatRepository;

    public Long createMemberStat(
        Member member, CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        MemberStatTest memberStat = memberStatRepository.save(
            MemberStatConverter_v2.toEntity(member.getId(), createMemberStatRequestDTO));

        //todo : memberStatEquality 저장 추가
        return memberStat.getMemberId();
    }
}
