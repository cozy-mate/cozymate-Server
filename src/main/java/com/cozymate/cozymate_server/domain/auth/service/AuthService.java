package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.Token;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.userDetails.TemporaryMember;
import com.cozymate.cozymate_server.domain.auth.utils.AuthConverter;
import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
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

    // 기존에 있는 회원이면 AccessToken, 임시회원이면 TempToken 발급
    public AuthResponseDTO.TokenResponseDTO generateTokenDTO(String clientId) {
        // 이미 회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            return generateMemberTokenDTO(clientId);
        }
        // 새로 가입한 경우
        return generateTemporaryTokenDTO(clientId);
    }

    public MemberDetails extractMemberDetailsInRefreshToken(String refreshToken) {
        String clientId = jwtUtil.extractUserName(refreshToken);
        return new MemberDetails(memberQueryService.findByClientId(clientId));
    }

    private AuthResponseDTO.TokenResponseDTO generateTemporaryTokenDTO(String clientId) {
        TemporaryMember temporaryMember = new TemporaryMember(clientId);

        MemberResponseDTO.MemberInfoDTO temporaryMemberInfo = generateTemporaryMemberInfo();

        String temporaryToken = generateTemporaryToken(temporaryMember);

        return AuthConverter.toTemporaryTokenResponseDTO(temporaryMemberInfo, TEMPORARY_TOKEN_SUCCESS_MESSAGE,
                temporaryToken);
    }

    private AuthResponseDTO.TokenResponseDTO generateMemberTokenDTO(String clientId) {
        MemberDetails memberDetails = loadUserByUsername(clientId);

        MemberResponseDTO.MemberInfoDTO memberInfoDTO = MemberConverter.toMemberInfoDTO(memberDetails.getMember());

        String accessToken = generateAccessToken(memberDetails);
        String refreshToken = generateRefreshToken(memberDetails);

        log.info("access token: {}", accessToken);
        log.info("refresh token: {}", refreshToken);

        return AuthConverter.toTokenResponseDTO(
                memberInfoDTO, MEMBER_TOKEN_MESSAGE, accessToken, refreshToken);
    }

    private MemberResponseDTO.MemberInfoDTO generateTemporaryMemberInfo() {
        return MemberConverter.toTemporaryInfoDTO("", "", "", "", 0);
    }

    private String getRefreshToken(UserDetails userDetails) {
        Token token = findToken(userDetails.getUsername());
        return token.getRefreshToken();
    }

    @Override
    public MemberDetails loadUserByUsername(String clientId) {
        return new MemberDetails(memberQueryService.findByClientId(clientId));
    }

    private String generateTemporaryToken(UserDetails userDetails) {
        return jwtUtil.generateTemporaryToken(userDetails);
    }

    private String generateAccessToken(UserDetails userDetails) {
        return jwtUtil.generateAccessToken(userDetails);
    }

    @Transactional
    String generateRefreshToken(UserDetails userDetails) {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        Token newToken = new Token(userDetails.getUsername(), refreshToken);
        tokenRepository.save(newToken);
        return refreshToken;
    }


    @Transactional
    void deleteRefreshToken(String clientId) {
        Token token = findToken(clientId);
        tokenRepository.delete(token);
    }

    @Transactional
    Token findToken(String clientId) {
        return tokenRepository.findById(clientId).orElseThrow(
                () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }
}