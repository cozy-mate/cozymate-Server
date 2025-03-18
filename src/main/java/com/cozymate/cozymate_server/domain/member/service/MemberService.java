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
import com.cozymate.cozymate_server.domain.member.dto.request.UpdateRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.WithdrawRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.validator.MemberValidator;
import com.cozymate.cozymate_server.domain.university.University;

import com.cozymate.cozymate_server.domain.university.repository.UniversityRepositoryService;
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
public class MemberService {

    // 의존성 주입
    private final AuthService authService;
    private final MemberRepositoryService memberRepositoryService;
    private final MemberValidator memberValidator;
    private final UniversityRepositoryService universityRepositoryService;

    private final MemberWithdrawService memberWithdrawService;

    private final MailService mailService;

    private final SignUpNotificationService signUpNotificationService;

    /**
     * 닉네임 유효성 검사 메서드
     *
     * @param nickname 유효성을 검사할 닉네임
     * @return 유효하면 true, 유효하지 않으면 false 반환
     */
    public Boolean checkNickname(String nickname) {
        //todo: nickname 금지어 로직 추가, redis 같은 거 써야 할듯
        try {
            memberValidator.checkNickname(nickname);  // 닉네임 유효성 검증
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
        if (memberRepositoryService.getExistenceByClientId(clientId)) {
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

        memberValidator.checkNickname(signUpRequestDTO.nickname());

        memberValidator.checkClientId(clientId);

        University memberUniversity = universityRepositoryService.getUniversityByIdOrThrow(
            signUpRequestDTO.universityId());

        memberValidator.checkMajorName(memberUniversity,signUpRequestDTO.majorName());

        Member member = memberRepositoryService.createMember(
            MemberConverter.toMember(clientId, signUpRequestDTO, memberUniversity));

        signUpNotificationService.sendSignUpNotification(member);

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
        memberValidator.checkNickname(nickname);
        member = memberRepositoryService.getMemberByIdOrThrow(member.getId());

        member.updateNickname(nickname);
    }

    @Transactional
    public void updatePersona(Member member, Integer persona) {
        member = memberRepositoryService.getMemberByIdOrThrow(member.getId());

        member.updatePersona(persona);
    }

    @Transactional
    public void updateBirthday(Member member, LocalDate birthday) {
        member = memberRepositoryService.getMemberByIdOrThrow(member.getId());

        member.updateBirthday(birthday);
    }

    @Transactional
    public void updateMajor(Member member, String majorName) {
        member = memberRepositoryService.getMemberByIdOrThrow(member.getId());
        memberValidator.checkMajorName(member.getUniversity(),majorName);

        member.updateMajor(majorName);
    }

    @Transactional
    public void update(Member member, UpdateRequestDTO requestDTO) {
        if(!member.getNickname().equals(requestDTO.nickname())){
            memberValidator.checkNickname(requestDTO.nickname());
        }
        memberValidator.checkMajorName(member.getUniversity(),requestDTO.majorName());

        member.update(
            requestDTO.nickname(),
            requestDTO.persona(),
            requestDTO.birthday(),
            requestDTO.majorName()
        );

        memberRepositoryService.updateMember(member);
    }

    /**
     * 사용자 회원탈퇴 메서드
     *
     * @param memberDetails 사용자 세부 정보
     */
    public void withdraw(WithdrawRequestDTO withdrawRequestDTO, MemberDetails memberDetails) {
        String withdrawReason = memberDetails.member().getNickname() + "님의 탈퇴 사유 입니다."
            + withdrawRequestDTO.withdrawReason();
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