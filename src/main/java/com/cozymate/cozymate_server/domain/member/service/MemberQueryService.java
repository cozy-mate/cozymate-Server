package com.cozymate.cozymate_server.domain.member.service;

import static com.cozymate.cozymate_server.global.response.code.status.ErrorStatus._MEMBER_NOT_FOUND;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
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
        // todo : 금지 닉네임 로직 추가
        return !memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    @Transactional
    public Member findByClientId(String clientId) {
        return memberRepository.findByClientId(clientId).orElseThrow(
                () -> new GeneralException(_MEMBER_NOT_FOUND)
        );
    }

    @Transactional
    public Boolean isPresent(String clientId) {
        return memberRepository.existsByClientId(clientId);
    }
}
