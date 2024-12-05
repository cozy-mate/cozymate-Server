package com.cozymate.cozymate_server.domain.mail.service;


import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.converter.MailConverter;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;
import com.cozymate.cozymate_server.domain.mail.repository.MailRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private static final Integer MAIL_AUTHENTICATION_EXPIRED_TIME = 30;
    private final JavaMailSender mailSender;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final AuthService authService;

    @Value("${spring.mail.username}")
    private static final String ADMIN_MAIL_USERNAME = "qnfn120";

    private static final String ADMIN_MAIL_DOMAIN = "@gmail.com"; // 관리자 이메일 주소


    @Transactional
    public void sendUniversityAuthenticationCode(MemberDetails memberDetails,
        MailSendRequestDTO sendDTO) {
        University university = universityRepository.findById(sendDTO.universityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        String mailAddress = sendDTO.mailAddress();
        validateMailAddress(mailAddress, university.getMailPattern());

        MailAuthentication mailAuthentication = createAndSendMail(memberDetails.member().getId(),
            mailAddress, university.getName());

        mailRepository.save(mailAuthentication);
    }

    @Transactional
    public VerifyResponseDTO verifyMemberUniversity(MemberDetails memberDetails,
        VerifyRequestDTO verifyDTO) {
        University memberUniversity = universityRepository.findById(verifyDTO.universityId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        memberDetails.member().verifyMemberUniversity(memberUniversity, verifyDTO.majorName());
        memberRepository.save(memberDetails.member());

        TokenResponseDTO tokenResponseDTO = authService.generateMemberTokenDTO(memberDetails);
        return MailConverter.toVerifyResponseDTO(tokenResponseDTO);
    }

    public String isVerified(Member member) {
        Optional<MailAuthentication> mailAuthentication = mailRepository.findById(member.getId());

        if (mailAuthentication.isPresent() && Boolean.TRUE.equals(
            mailAuthentication.get().getIsVerified())) {
            return mailAuthentication.get().getMailAddress();
        }
        return "";
    }

    public void sendCustomMailToAdmin(String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(ADMIN_MAIL_USERNAME + ADMIN_MAIL_DOMAIN);
            helper.setSubject(subject);
            helper.setText(content, true); // 전달받은 content를 그대로 전송
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus._MAIL_SEND_FAIL);
        }
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

    private MailAuthentication createAndSendMail(Long memberId, String mailAddress,
        String universityName) {

        String authenticationCode = Base64.getEncoder()
            .encodeToString(UUID.randomUUID().toString().getBytes())
            .substring(0, 6);

        String emailBody = makeMailBody(authenticationCode, universityName);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(mailAddress);
            helper.setSubject("cozymate 대학교 메일인증");
            helper.setText(emailBody, true);
            mailSender.send(message);

            return MailConverter.toMailAuthenticationWithParams(memberId, mailAddress,
                authenticationCode, false);
        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus._MAIL_SEND_FAIL);
        }
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

    private String makeMailBody(String authenticationCode, String universityName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                "mail/mail_form.html");

            if (inputStream == null) {
                throw new GeneralException(ErrorStatus._CANNOT_FIND_MAIL_FORM);
            }
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            template = template.replace("{{universityName}}", universityName)
                .replace("{{authenticationCode}}", authenticationCode);

            return template;
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus._CANNOT_FIND_MAIL_FORM);
        }
    }

}
