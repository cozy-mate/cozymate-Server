package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatCommandServiceImpl implements MemberStatCommandService{

    private final MemberStatRepository memberStatRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long createMemberStat(Long memberId, MemberStatRequestDTO memberStatRequestDTO) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(()->new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        MemberStat saveMemberStat = memberStatRepository.save(MemberStat.of(member,memberStatRequestDTO));
        return saveMemberStat.getId();
    }
}
