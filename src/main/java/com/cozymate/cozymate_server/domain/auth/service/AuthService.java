package com.cozymate.cozymate_server.domain.auth.service;

import static com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.RE_LOGIN_EXISTING_MEMBER_MESSAGE;
import static com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.TEMPORARY_TOKEN_SUCCESS_MESSAGE;

import com.cozymate.cozymate_server.domain.auth.Token;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.utils.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.utils.TemporaryMember;
import com.cozymate.cozymate_server.domain.auth.utils.jwt.JwtUtil;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import java.util.NoSuchElementException;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Service
public class AuthService implements UserDetailsService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;


    // 기존에 있는 회원이면 AccessToken, 임시회원이면 TempToken 발급
    @Transactional
    public String generateToken(String clientId) {
        UserDetails userDetails = loadUserByUsername(clientId);
        Optional<Member> member = memberRepository.findByClientId(clientId);
        if (member.isPresent()) {
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            Token newToken = new Token(clientId, refreshToken);
            tokenRepository.save(newToken);

            return accessToken;
        } else {
            return jwtUtil.generateTemporaryToken(userDetails);
        }
    }

    public AuthResponseDTO.SocialLoginDTO socialLogin(String clientId, String token) {
        if (jwtUtil.isTemporaryToken(token)) {
            return AuthResponseDTO.SocialLoginDTO.builder()
                    .message(TEMPORARY_TOKEN_SUCCESS_MESSAGE)
                    .build();
        }
        return AuthResponseDTO.SocialLoginDTO.builder()
                .message(RE_LOGIN_EXISTING_MEMBER_MESSAGE)
                .refreshToken(getRefreshToken(loadUserByUsername(clientId)))
                .build();
    }

    public String getRefreshToken(UserDetails userDetails) {
        Token token = tokenRepository.findById(userDetails.getUsername()).orElseThrow();
        return token.getRefreshToken();
    }

    @Transactional

    public void deleteRefreshToken(String userName) {
        Token token = tokenRepository.findById(userName).orElseThrow();
        // todo : 예외처리
        tokenRepository.delete(token);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) {
        try {
            Member member = memberRepository.findByClientId(userName).orElseThrow();
            return new MemberDetails(member);
        } catch (NoSuchElementException e) {
            return new TemporaryMember(userName);
        }
    }
}
