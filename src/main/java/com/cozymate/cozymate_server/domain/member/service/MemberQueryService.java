package com.cozymate.cozymate_server.domain.member.service;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
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
    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9_]*$";

    @Transactional
    public void isValidNickName(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_EXISTING);
        }

        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new GeneralException(ErrorStatus._INVALID_NICKNAME_PATTERN);
        }

        if (nickname.length() < 2 || nickname.length() > 8) {
            throw new GeneralException(ErrorStatus._INVALID_NICKNAME_LENGTH);
        }
    }

    @Transactional
    public Member findByClientId(String clientId) {
        return memberRepository.findByClientId(clientId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
    }


    @Transactional
    public Boolean isPresent(String clientId) {
        return memberRepository.existsByClientId(clientId);
    }
}
