package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.service.MailService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.request.SignUpRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.UpdateRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.WithdrawRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.validator.MemberValidator;
import com.cozymate.cozymate_server.domain.university.University;

import com.cozymate.cozymate_server.domain.university.repository.UniversityRepositoryService;
import com.cozymate.cozymate_server.domain.university.validator.UniversityValidator;

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
    private final UniversityValidator universityValidator;
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
    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        //todo: nickname 금지어 로직 추가
        return memberValidator.isValidNickname(nickname);
    }


    /**
     * 사용자 정보 조회 메서드
     *
     * @param memberDetails 사용자 세부 정보
     * @return 사용자 정보를 담은 DTO
     */
    @Transactional(readOnly = true)
    public MemberDetailResponseDTO getMemberDetailInfo(MemberDetails memberDetails) {
        return MemberConverter.toMemberDetailResponseDTOFromEntity(memberDetails.member());
    }

    /**
     * 사용자 회원가입 메서드
     *
     * @param clientId         사용자 식별자 clientId 로 사용자 중복 검증
     * @param signUpRequestDTO 회원가입 요청 정보를 담은 DTO
     * @return 로그인 결과를 담은 DTO
     */
    @Transactional
    public SignInResponseDTO signUp(String clientId,
        SignUpRequestDTO signUpRequestDTO) {

        memberValidator.checkNickname(signUpRequestDTO.nickname());

        memberValidator.checkClientId(clientId);

        University memberUniversity = universityRepositoryService.getUniversityByIdOrThrow(
            signUpRequestDTO.universityId());

        universityValidator.checkMajorName(memberUniversity,signUpRequestDTO.majorName());

        Member member = memberRepositoryService.createMember(
            MemberConverter.toMember(clientId, signUpRequestDTO, memberUniversity));

        signUpNotificationService.sendSignUpNotification(member);

        // 기존 회원으로 로그인 처리
        return authService.signInByExistingMember(clientId);
    }


    @Transactional
    public void update(Member member, UpdateRequestDTO requestDTO) {
        if(!member.getNickname().equals(requestDTO.nickname())){
            memberValidator.checkNickname(requestDTO.nickname());
        }
        universityValidator.checkMajorName(member.getUniversity(),requestDTO.majorName());

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
    @Transactional
    public void withdraw(WithdrawRequestDTO withdrawRequestDTO, MemberDetails memberDetails) {
        String withdrawReason = memberDetails.member().getNickname() + "님의 탈퇴 사유 입니다."
            + withdrawRequestDTO.withdrawReason();
        String mailSubject = memberDetails.member().getNickname() + "탈퇴 사유";

        mailService.sendCustomMailToAdmin(mailSubject, withdrawReason);
        memberWithdrawService.withdraw(memberDetails.member());
    }
}