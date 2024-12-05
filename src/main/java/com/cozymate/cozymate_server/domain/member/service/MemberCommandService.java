package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.mail.service.MailService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.request.SignInRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.SignUpRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.WithdrawRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {

    // 의존성 주입
    private final AuthService authService;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;

    private final MemberWithdrawService memberWithdrawService;

    private final MailService mailService;

    /**
     * 닉네임 유효성 검사 메서드
     *
     * @param nickname 유효성을 검사할 닉네임
     * @return 유효하면 true, 유효하지 않으면 false 반환
     */
    public Boolean checkNickname(String nickname) {
        //todo: nickname 금지어 로직 추가, redis 같은 거 써야 할듯
        try {
            memberQueryService.isValidNickName(nickname);  // 닉네임 유효성 검증
            return true;  // 검증 성공 시
        } catch (IllegalArgumentException e) {
            return false;  // 예외 메시지 반환
        }
    }

    /**
     * 사용자 로그인 메서드
     *
     * @param signInRequestDTO 로그인 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    public SignInResponseDTO signIn(SignInRequestDTO signInRequestDTO) {
        // 소셜 타입 검증 및 조회
        SocialType socialType = SocialType.getValue(signInRequestDTO.socialType());

        String clientId = ClientIdMaker.makeClientId(signInRequestDTO.clientId(), socialType);

        log.debug("사용자 로그인 : {}", clientId);
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
     * @param clientId         사용자 식별자 clientId 로 사용자 중복 검증
     * @param signUpRequestDTO 회원가입 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    public SignInResponseDTO signUp(String clientId,
        SignUpRequestDTO signUpRequestDTO) {

        if (!checkNickname(signUpRequestDTO.nickname())) {
            throw new GeneralException(ErrorStatus._NICKNAME_EXISTING);
        }
        if (memberQueryService.isPresent(clientId)) { // 사용자 중복 검증
            throw new GeneralException(ErrorStatus._MEMBER_EXISTING);
        }
        University memberUniversity = universityRepository.findById(signUpRequestDTO.universityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        memberRepository.save(
            MemberConverter.toMember(clientId, signUpRequestDTO, memberUniversity));

        // 기존 회원으로 로그인 처리
        return signInByExistingMember(clientId);
    }

    /**
     * 사용자 정보 조회 메서드
     *
     * @param memberDetails 사용자 세부 정보
     * @return 사용자 정보를 담은 DTO
     */
    public MemberDetailResponseDTO getMemberDetailInfo(MemberDetails memberDetails) {
        return MemberConverter.toMemberDetailResponseDTOFromEntity(memberDetails.member());
    }


    @Transactional
    public void updateNickname(Member member, String nickname) {
        member = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        if (!checkNickname(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_EXISTING);
        }
        member.updateNickname(nickname);
    }

    @Transactional
    public void updatePersona(Member member, Integer persona) {
        member = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.updatePersona(persona);
    }

    @Transactional
    public void updateBirthday(Member member, LocalDate birthday) {
        member = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.updateBirthday(birthday);
    }

    @Transactional
    public void updateMajor(Member member, String majorName) {
        member = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.updateMajor(majorName);
    }


    /**
     * 사용자 회원탈퇴 메서드
     *
     * @param memberDetails 사용자 세부 정보
     */
    public void withdraw(WithdrawRequestDTO withdrawRequestDTO, MemberDetails memberDetails) {
        String withdrawReason = withdrawRequestDTO.withdrawReason();
        String mailSubject = memberDetails.member().getNickname() + "탈퇴 사유";

        mailService.sendCustomMailToAdmin(mailSubject, withdrawReason);
        memberWithdrawService.withdraw(memberDetails.member());
    }

    /**
     * 기존 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    private SignInResponseDTO signInByExistingMember(String clientId) {
        // 사용자 세부 정보를 로드
        MemberDetails memberDetails = authService.loadMember(clientId);

        // 사용자 정보를 DTO로 변환
        MemberDetailResponseDTO memberDetailResponseDTO = MemberConverter.toMemberDetailResponseDTOFromEntity(
            memberDetails.member());

        // 인증 토큰 생성 및 DTO로 변환
        TokenResponseDTO tokenResponseDTO = authService.generateMemberTokenDTO(memberDetails);

        // 로그인 응답 DTO 반환
        return MemberConverter.toSignInResponseDTO(memberDetailResponseDTO, tokenResponseDTO);
    }

    /**
     * 임시 회원 로그인 처리 메서드
     *
     * @param clientId 사용자 식별자
     * @return 로그인 결과를 담은 DTO
     */
    private SignInResponseDTO signInByTemporaryMember(String clientId) {
        // 임시 인증 토큰 생성 및 DTO로 변환
        TokenResponseDTO tokenResponseDTO = authService.generateTemporaryTokenDTO(clientId);

        // 임시 로그인 응답 DTO 반환
        return MemberConverter.toTemporarySignInResponseDTO(tokenResponseDTO);
    }
}