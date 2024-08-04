package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberStatQueryService {

    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;

    public MemberStat getMemberStat(Long memberId){

        memberRepository.findById(memberId).orElseThrow(
            ()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        return memberStatRepository.findByMemberId(memberId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );
    }
}
