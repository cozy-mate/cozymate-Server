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

    public static final String MEMBER_TOKEN_MESSAGE = "기존 사용자 토큰 발급 성공";

    public static final String TOKEN_HEADER_NAME = "Authorization";

    private final JwtUtil jwtUtil;
    private final MemberQueryService memberQueryService;
    private final TokenRepository tokenRepository;


    // accessToken, refreshToken 둘다 생성
    public HttpHeaders generateTokenHeader(String clientId) {
        String token = generateToken(clientId);
        return addTokenAtHeader(token);
    }

    public MemberDetails extractMemberDetailsInRefreshToken(String refreshToken) {
        String clientId = jwtUtil.extractUserName(refreshToken);
        return new MemberDetails(memberQueryService.findByClientId(clientId));
    }

    public AuthResponseDTO.TokenResponseDTO socialLogin(String clientId) {
        // 이미 회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            MemberDetails memberDetails = loadMember(clientId);
            return generateMemberResponse(memberDetails);
        }
        // 새로 가입한 경우
        MemberResponseDTO.MemberInfoDTO temporaryMemberInfo = generateTemporaryMember();
        return AuthConverter.toTokenResponseDTO(temporaryMemberInfo, TEMPORARY_TOKEN_SUCCESS_MESSAGE,
                clientId);
    }
    public AuthResponseDTO.TokenResponseDTO generateMemberResponse(MemberDetails memberDetails) {
        MemberResponseDTO.MemberInfoDTO memberInfoDTO = MemberConverter.toMemberInfoDTO(memberDetails.getMember());
        return AuthConverter.toTokenResponseDTO(
                memberInfoDTO, MEMBER_TOKEN_MESSAGE, getRefreshToken(memberDetails));
    }

    private HttpHeaders addTokenAtHeader(String token) {
        log.info("발급된 토큰 :" + jwtUtil.extractTokenType(token) + ":" + token);
        HttpHeaders headers = new HttpHeaders();
        headers.add(TOKEN_HEADER_NAME, JwtUtil.TOKEN_PREFIX + token);
        return headers;
    }

    // 기존에 있는 회원이면 AccessToken, 임시회원이면 TempToken 발급
    private String generateToken(String clientId) {
        UserDetails userDetails = loadUserByUsername(clientId);
        // 이미 회원인 경우
        if (memberQueryService.isPresent(clientId)) {
            return generateTokenPair(userDetails);
        }
        // 새로 가입한 경우
        return jwtUtil.generateTemporaryToken(userDetails);
    }

    private MemberResponseDTO.MemberInfoDTO generateTemporaryMember() {
        return MemberConverter.toTemporaryInfoDTO("", "", "", "", 0);
    }

    private String getRefreshToken(UserDetails userDetails) {
        Token token = findToken(userDetails.getUsername());
        return token.getRefreshToken();
    }

    private MemberDetails loadMember(String clientId) {
        return new MemberDetails(memberQueryService.findByClientId(clientId));
    }

    @Override
    public UserDetails loadUserByUsername(String clientId) {
        if (memberQueryService.isPresent(clientId)) {
            return loadMember(clientId);
        } else {
            return new TemporaryMember(clientId);
        }
    }
    @Transactional
    String generateTokenPair(UserDetails userDetails) {
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        Token newToken = new Token(userDetails.getUsername(), refreshToken);
        tokenRepository.save(newToken);
        return accessToken;
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