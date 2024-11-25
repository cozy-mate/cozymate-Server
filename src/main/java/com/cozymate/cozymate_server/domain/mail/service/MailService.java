package com.cozymate.cozymate_server.domain.mail.service;


import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.converter.MailConverter;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;
import com.cozymate.cozymate_server.domain.mail.repository.MailRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.service.MemberCommandService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private static final Integer MAIL_AUTHENTICATION_EXPIRED_TIME = 30;
    private final JavaMailSender mailSender;
    private final MailRepository mailRepository;
    private final MemberCommandService memberCommandService;
    private final UniversityRepository universityRepository;

    @Transactional
    public void sendUniversityAuthenticationCode(MemberDetails memberDetails,
        MailSendRequestDTO sendDTO) {
        University university = universityRepository.findById(sendDTO.universityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        String mailAddress = sendDTO.mailAddress();
        validateMailAddress(mailAddress, university.getMailPattern());

        MailAuthentication mailAuthentication = createAndSendMail(memberDetails.member().getId(),
            mailAddress);

        mailRepository.save(mailAuthentication);
    }

    @Transactional
    public VerifyResponseDTO verifyMemberUniversity(MemberDetails memberDetails,
        VerifyRequestDTO verifyDTO) {
        Member member = memberDetails.member();

        verifyAuthenticationCode(member, verifyDTO.code());

        TokenResponseDTO tokenResponseDTO = memberCommandService.verifyMemberUniversity(
            memberDetails,
            verifyDTO.universityId(),
            verifyDTO.majorName());
        return MailConverter.toVerifyResponseDTO(tokenResponseDTO);
    }

    public String isVerified(Member member) {
        Optional<MailAuthentication> mailAuthentication = mailRepository.findById(member.getId());

        if (mailAuthentication.isPresent() && Boolean.TRUE.equals(mailAuthentication.get().getIsVerified())) {
            return mailAuthentication.get().getMailAddress();
        }
        return "";
    }

    private void verifyAuthenticationCode(Member member, String requestCode) {

        MailAuthentication mailAuthentication = mailRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(
                ErrorStatus._MAIL_AUTHENTICATION_NOT_FOUND));
        // 만료 시간 초과 여부 확인
        if (LocalDateTime.now()
            .isAfter(
                mailAuthentication.getUpdatedAt().plusMinutes(MAIL_AUTHENTICATION_EXPIRED_TIME))) {
            throw new GeneralException(ErrorStatus._MAIL_AUTHENTICATION_CODE_EXPIRED);
        }

        // 메일 인증 코드 일치 여부 확인
        if (!mailAuthentication.getCode().equals(requestCode)) {
            throw new GeneralException(ErrorStatus._MAIL_AUTHENTICATION_CODE_INCORRECT);
        }

        mailAuthentication.verify();
    }

    private MailAuthentication createAndSendMail(Long memberId, String mailAddress) {
        SimpleMailMessage message = new SimpleMailMessage();

        String authenticationCode = Base64.getEncoder()
            .encodeToString(UUID.randomUUID().toString().getBytes())
            .substring(0, 6);

        message.setTo(mailAddress);
        message.setSubject("COZYMATE 대학교 메일인증");
        message.setText("COZYMATE 대학교 메일인증 코드입니다 : " + authenticationCode);
        mailSender.send(message);

        return MailConverter.toMailAuthenticationWithParams(memberId, mailAddress,
            authenticationCode, false);
    }

    private void validateMailAddress(String mailAddress, String mailPattern) {
        Optional<MailAuthentication> mailAuthentication = mailRepository.findByMailAddress(
            mailAddress);

        if (!mailAddress.contains(mailPattern)) {
            throw new GeneralException(ErrorStatus._INVALID_MAIL_ADDRESS_DOMAIN);
        }
        if (mailAuthentication.isPresent() && mailAuthentication.get().getIsVerified()) {
            throw new GeneralException(ErrorStatus._MAIL_ADDRESS_DUPLICATED);
        }
    }

}
