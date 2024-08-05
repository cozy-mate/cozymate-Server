package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberRepository memberRepository;

    @Transactional
    public Boolean isValidNickName(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public Member save(Member member) {
        memberRepository.save(member);
        log.info(member.getNickname() + "저장 완료");
        return member;
    }
}
