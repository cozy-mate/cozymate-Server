package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.Token;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.userDetails.TemporaryMember;
import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;

import com.cozymate.cozymate_server.domain.member.service.MemberQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Service
public class AuthService implements UserDetailsService {

    public static final String TEMPORARY_TOKEN_SUCCESS_MESSAGE = "임시 토큰 발급 완료";

    public static final String RE_LOGIN_EXISTING_MEMBER_MESSAGE = "기존 사용자 재로그인";

    private final JwtUtil jwtUtil;
    private final MemberQueryService memberQueryService;
    private final TokenRepository tokenRepository;


    // 기존에 있는 회원이면 AccessToken, 임시회원이면 TempToken 발급
    @Transactional
    public String generateToken(String clientId) {
        UserDetails userDetails = loadUserByUsername(clientId);
        // 이미 회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            Token newToken = new Token(clientId, refreshToken);
            tokenRepository.save(newToken);
            return accessToken;
        }
        // 새로 가입한 경우
        return jwtUtil.generateTemporaryToken(userDetails);
    }

    public HttpHeaders addTokenAtHeader(String token) {
        log.info("발급된 토큰 :" + jwtUtil.extractTokenType(token) + ":" + token);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.TOKEN_PREFIX, token);
        return headers;
    }

    public AuthResponseDTO.SocialLoginDTO socialLogin(String clientId) {
        // 이미 회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            return AuthResponseDTO.SocialLoginDTO.builder()
                    .message(RE_LOGIN_EXISTING_MEMBER_MESSAGE)
                    .refreshToken(getRefreshToken(loadUserByUsername(clientId)))
                    .build();
        }
        // 새로 가입한 경우
        return AuthResponseDTO.SocialLoginDTO.builder()
                .message(TEMPORARY_TOKEN_SUCCESS_MESSAGE)
                .build();
    }

    public String getRefreshToken(UserDetails userDetails) {
        Token token = findToken(userDetails.getUsername());
        return token.getRefreshToken();
    }

    public void deleteRefreshToken(String clientId) {
        Token token = findToken(clientId);
        tokenRepository.delete(token);
    }

    @Override
    public UserDetails loadUserByUsername(String clientId) {
        if (memberQueryService.isPresent(clientId)) {
            return new MemberDetails(memberQueryService.findByClientId(clientId));
        } else {
            return new TemporaryMember(clientId);
        }
    }

    @Transactional
    Token findToken(String clientId) {
        return tokenRepository.findById(clientId).orElseThrow(
                () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }
}
