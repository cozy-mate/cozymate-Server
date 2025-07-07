package com.cozymate.cozymate_server.auth.service;

import com.cozymate.cozymate_server.auth.Token;
import com.cozymate.cozymate_server.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.auth.utils.AuthConverter;
import com.cozymate.cozymate_server.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.auth.dto.response.TokenResponseDTO;
import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.auth.userdetails.TemporaryMember;
import com.cozymate.cozymate_server.auth.utils.JwtUtil;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.auth.dto.request.SignInRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.validator.MemberValidator;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    public static final String TEMPORARY_TOKEN_SUCCESS_MESSAGE = "임시 토큰 발급 완료";
    public static final String MEMBER_TOKEN_MESSAGE = "기존 사용자 토큰 발급 성공";
    private final JwtUtil jwtUtil;
    private final MemberRepositoryService memberRepositoryService;
    private final TokenRepository tokenRepository;
    private final MemberValidator memberValidator;

    public SignInResponseDTO reissue(String refreshToken) {
        String clientId = jwtUtil.extractUserName(refreshToken);

        validateRefreshToken(refreshToken);

        return signInByExistingMember(clientId);
    }


    /**
     * 사용자 로그인 메서드
     *
     * @param signInRequestDTO 로그인 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    @Transactional
    public SignInResponseDTO signIn(SignInRequestDTO signInRequestDTO) {
        // 소셜 타입 검증 및 조회
        SocialType socialType = SocialType.getValue(signInRequestDTO.socialType());

        String clientId = ClientIdMaker.makeClientId(signInRequestDTO.clientId(), socialType);

        log.debug("사용자 로그인 : {}", clientId);
        // 사용자가 기존 회원인지 확인하고, 로그인 처리
        if (memberRepositoryService.getExistenceByClientId(clientId)) {
            return signInByExistingMember(clientId);
        }

        // 신규회원인 경우 임시토큰 발급.
        return signInByTemporaryMember(clientId);

    }

    /**
     * 기존 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    public SignInResponseDTO signInByExistingMember(String clientId) {
        // 사용자 세부 정보를 로드
        MemberDetails memberDetails = loadMember(clientId);

        // 사용자 정보를 DTO로 변환
        MemberDetailResponseDTO memberDetailResponseDTO = MemberConverter.toMemberDetailResponseDTOFromEntity(
            memberDetails.member());

        // 인증 토큰 생성 및 DTO로 변환
        TokenResponseDTO tokenResponseDTO = generateMemberTokenDTO(memberDetails);

        // 로그인 응답 DTO 반환
        return MemberConverter.toSignInResponseDTO(memberDetailResponseDTO, tokenResponseDTO);
    }

    public SignInResponseDTO signInByPreMember(String clientId,
        University memberUniversity, String majorName) {

        memberValidator.checkClientId(clientId);
        memberRepositoryService.createMember(
            MemberConverter.toPreMember(clientId, memberUniversity, majorName));

        return signInByExistingMember(clientId);
    }
    /**
     * 임시 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    private SignInResponseDTO signInByTemporaryMember(String clientId) {
        // 임시 인증 토큰 생성 및 DTO로 변환
        TokenResponseDTO tokenResponseDTO = generateTemporaryTokenDTO(clientId);

        // 임시 로그인 응답 DTO 반환
        return MemberConverter.toTemporarySignInResponseDTO(tokenResponseDTO);
    }

    // 회원가입을 하려는 경우
    // 임시 토큰을 만들어서 token response dto 만들어 반환
    // 바디에 임시 토큰값 있음
    private TokenResponseDTO generateTemporaryTokenDTO(String clientId) {
        TemporaryMember temporaryMember = loadTemporaryMember(clientId);

        String temporaryToken = jwtUtil.generateTemporaryToken(temporaryMember);

        return AuthConverter.toTemporaryTokenResponseDTO(TEMPORARY_TOKEN_SUCCESS_MESSAGE,
            temporaryToken);
    }

    private TokenResponseDTO generatePreMemberToken(MemberDetails memberDetails) {
        String temporaryToken = jwtUtil.generateTemporaryToken(memberDetails);

        return AuthConverter.toTemporaryTokenResponseDTO(TEMPORARY_TOKEN_SUCCESS_MESSAGE,
            temporaryToken);
    }

    // 기존 회원인 경우
    // access token 과 refresh token을 만들어 token response dto 반환
    // 바디에 access token, refresh token 이 있음
    // refresh token은 db에 저장
    public TokenResponseDTO generateMemberTokenDTO(MemberDetails memberDetails) {
        if (memberDetails.member().getRole().equals(Role.PRE_USER)) {
            return generatePreMemberToken(memberDetails);
        }

        String accessToken = jwtUtil.generateAccessToken(memberDetails);
        String refreshToken = jwtUtil.generateRefreshToken(memberDetails);

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
        if (memberRepositoryService.getExistenceByClientId(clientId)) {
            return loadMember(clientId);
        }
        // 임시 회원인 경우
        return loadTemporaryMember(clientId);

    }

    // refresh token을 redis 에서 삭제
    public void deleteRefreshToken(String clientId) {
        Token token = findRefreshTokenByClientId(clientId);
        tokenRepository.delete(token);
    }

    // 기존회원인 경우
    // 회원을 찾아 그 정보로 MemberDetails 만드는 함수
    private MemberDetails loadMember(String clientId) {
        return new MemberDetails(memberRepositoryService.getMemberByClientIdOrThrow(clientId));
    }

    // 임시 회원인 경우
    // client id 로 temporary member 만들어서 반환
    private TemporaryMember loadTemporaryMember(String clientId) {
        return new TemporaryMember(clientId);
    }

    // refresh token을 db에 저장하는 함수
    private void saveRefreshToken(String clientId, String refreshToken) {
        Token newToken = new Token(clientId, refreshToken);
        tokenRepository.save(newToken);
    }

    // refresh token db 에서 찾아보기
    private Token findRefreshTokenByClientId(String clientId) {
        return tokenRepository.findById(clientId).orElseThrow(
            () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }

    private void validateRefreshToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken).orElseThrow(
            () -> new GeneralException(ErrorStatus._TOKEN_NOT_FOUND)
        );
    }

}