package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {
    // 의존성 주입
    private final AuthService authService;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    /**
     * 닉네임 유효성 검사 메서드
     *
     * @param nickname 유효성을 검사할 닉네임
     * @return 유효하면 true, 유효하지 않으면 false 반환
     */
    public Boolean checkNickname(String nickname) {
        //todo: nickname 금지어 로직 추가, redis 같은 거 써야 할듯
        return memberQueryService.isValidNickName(nickname);
    }

    /**
     * 사용자 로그인 메서드
     *
     * @param signInRequestDTO 로그인 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    public MemberResponseDTO.SignInResponseDTO signIn(MemberRequestDTO.SignInRequestDTO signInRequestDTO) {
        // 소셜 타입 검증 및 조회
        SocialType socialType = SocialType.getValue(signInRequestDTO.getSocialType())
                .orElseThrow(() -> new GeneralException(ErrorStatus._INVALID_SOCIAL_TYPE));

        String clientId = ClientIdMaker.makeClientId(signInRequestDTO.getClientId(), socialType);

        log.info(clientId);
        // 사용자가 기존 회원인지 확인하고, 로그인 처리
        if (memberQueryService.isPresent(clientId)) {
            return signInByExistingMember(clientId);
        }

        // 신규회원인 경우 임시토큰 발급.
        return signInByTemporaryMember(clientId);

    }

    /**
     * 사용자 회원가입 메서드
     *
     * @param clientId 사용자 식별자
     * clientId 로 사용자 중복 검증
     * @param signUpRequestDTO 회원가입 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    public MemberResponseDTO.SignInResponseDTO signUp(String clientId,
                                                      MemberRequestDTO.SignUpRequestDTO signUpRequestDTO) {

        if (memberQueryService.isPresent(clientId)) { // 사용자 중복 검증
            throw new GeneralException(ErrorStatus._MEMBER_EXISTING);
        }
        // 회원 정보를 Member 엔티티로 변환하고 저장
        Member member = MemberConverter.toMember(clientId, signUpRequestDTO);
        memberRepository.save(member);

        // 기존 회원으로 로그인 처리
        return signInByExistingMember(clientId);
    }

    /**
     * 사용자 정보 조회 메서드
     *
     * @param memberDetails 사용자 세부 정보
     * @return 사용자 정보를 담은 DTO
     */
    public MemberResponseDTO.MemberInfoDTO getMemberInfo(MemberDetails memberDetails) {
        return MemberConverter.toMemberInfoDTO(memberDetails.getMember());
    }

    public AuthResponseDTO.TokenResponseDTO verifyMember(MemberDetails memberDetails){
        memberDetails.getMember().verify();
        memberRepository.save(memberDetails.getMember());

        return authService.generateMemberTokenDTO(memberDetails);
    }

    /**
     * 사용자 회원탈퇴 메서드
     *
     * @param memberDetails 사용자 세부 정보
     */

    // todo: 탈퇴로직 고도화
    public void withdraw(MemberDetails memberDetails) {
        // 리프레시 토큰 삭제 및 회원 삭제
        authService.deleteRefreshToken(memberDetails.getUsername());
        memberRepository.delete(memberDetails.getMember());
    }

    /**
     * 기존 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    private MemberResponseDTO.SignInResponseDTO signInByExistingMember(String clientId) {
        // 사용자 세부 정보를 로드
        MemberDetails memberDetails = authService.loadMember(clientId);

        // 사용자 정보를 DTO로 변환
        MemberResponseDTO.MemberInfoDTO memberInfoDTO = MemberConverter.toMemberInfoDTO(memberDetails.getMember());

        // 인증 토큰 생성 및 DTO로 변환
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = authService.generateMemberTokenDTO(memberDetails);

        // 로그인 응답 DTO 반환
        return MemberConverter.toLoginResponseDTO(memberInfoDTO, tokenResponseDTO);
    }

    /**
     * 임시 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    private MemberResponseDTO.SignInResponseDTO signInByTemporaryMember(String clientId) {
        // 임시 인증 토큰 생성 및 DTO로 변환
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = authService.generateTemporaryTokenDTO(clientId);

        // 임시 로그인 응답 DTO 반환
        return MemberConverter.toTemporaryLoginResponseDTO(tokenResponseDTO);
    }
}