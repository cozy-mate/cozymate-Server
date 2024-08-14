package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.Token;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.userDetails.TemporaryMember;
import com.cozymate.cozymate_server.domain.auth.utils.AuthConverter;
import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;
import com.cozymate.cozymate_server.domain.member.service.MemberQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

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

    public static final String TEMPORARY_TOKEN_SUCCESS_MESSAGE = "임시 토큰 발급 완료";

    public static final String MEMBER_TOKEN_MESSAGE = "기존 사용자 토큰 발급 성공";
    private final JwtUtil jwtUtil;
    private final MemberQueryService memberQueryService;
    private final TokenRepository tokenRepository;

    public AuthResponseDTO.TokenResponseDTO reissue(String refreshToken) {
        String clientId = jwtUtil.extractUserName(refreshToken);

        findTokenByValue(refreshToken);

        return generateMemberTokenDTO(loadMember(clientId));
    }

    // 회원가입을 하려는 경우
    // 임시 토큰을 만들어서 token response dto 만들어 반환
    // 바디에 임시 토큰값 있음
    public AuthResponseDTO.TokenResponseDTO generateTemporaryTokenDTO(String clientId) {
        TemporaryMember temporaryMember = loadTemporaryMember(clientId);

        String temporaryToken = jwtUtil.generateTemporaryToken(temporaryMember);

        return AuthConverter.toTemporaryTokenResponseDTO(TEMPORARY_TOKEN_SUCCESS_MESSAGE,
                temporaryToken);
    }

    // 기존 회원인 경우
    // access token 과 refresh token을 만들어 token response dto 반환
    // 바디에 access token, refresh token 이 있음
    // refresh token은 db에 저장
    public AuthResponseDTO.TokenResponseDTO generateMemberTokenDTO(MemberDetails memberDetails) {
        String accessToken = jwtUtil.generateAccessToken(memberDetails);
        String refreshToken = jwtUtil.generateRefreshToken(memberDetails);

        deleteRefreshToken(memberDetails.getUsername());
        saveRefreshToken(memberDetails.getUsername(), refreshToken);

        log.info("access token: {}", accessToken);
        log.info("refresh token: {}", refreshToken);

        return AuthConverter.toTokenResponseDTO(
                MEMBER_TOKEN_MESSAGE, accessToken, refreshToken);
    }


    // refresh token 에서 member details (user details 구현체) 추출
    public MemberDetails extractMemberDetailsInRefreshToken(String refreshToken) {
        String clientId = jwtUtil.extractUserName(refreshToken);
        return loadMember(clientId);
    }

    // client id 로 UserDetails 반환하는 함수.
    @Override
    public UserDetails loadUserByUsername(String clientId) {
        // 기존회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            return loadMember(clientId);
        }
        // 임시 회원인 경우
        return loadTemporaryMember(clientId);

    }

    // 기존회원인 경우
    // 회원을 찾아 그 정보로 MemberDetails 만드는 함수
    public MemberDetails loadMember(String clientId) {
        return new MemberDetails(memberQueryService.findByClientId(clientId));
    }

    // 임시 회원인 경우
    // client id 로 temporary member 만들어서 반환
    private TemporaryMember loadTemporaryMember(String clientId) {
        return new TemporaryMember(clientId);
    }

    // refresh token을 db에 저장하는 함수
    @Transactional
    void saveRefreshToken(String clientId, String refreshToken) {
        Token newToken = new Token(clientId, refreshToken);
        tokenRepository.save(newToken);
    }

    // refresh token을 db 에서 삭제
    @Transactional
    public void deleteRefreshToken(String clientId) {
        Token token = findRefreshTokenByClientId(clientId);
        tokenRepository.delete(token);
    }

    // refresh token db 에서 찾아보기
    @Transactional
    Token findRefreshTokenByClientId(String clientId) {
        return tokenRepository.findById(clientId).orElseThrow(
                () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }

    @Transactional
    Token findTokenByValue(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }

}