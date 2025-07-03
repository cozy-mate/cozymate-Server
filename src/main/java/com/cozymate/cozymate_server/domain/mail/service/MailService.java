package com.cozymate.cozymate_server.domain.mail.service;


import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.converter.MailConverter;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;

import com.cozymate.cozymate_server.domain.mail.repository.MailAuthenticationRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MailAuthenticationRepositoryService mailAuthenticationRepositoryService;
    private final UniversityRepositoryService universityRepositoryService;
    private final AuthService authService;


    private static final int MAIL_AUTHENTICATION_EXPIRED_TIME = 30;
    private static final String ADMIN_MAIL_ADDRESS = "cozymate0@gmail.com";
    private static final String CONFUSION_CHARACTERS_REGEX = "[IlOo0]";

    private static final String FROM = "cozymate_service <cozymate0@gmail.com>";
    private static final String EMAIL_SUBJECT = "cozymate 대학교 메일인증";
    private static final String MAIL_TEMPLATE_PATH = "mail/mail_form.html";
    private static final String LOGO_IMAGE_PATH = "mail/logo.png";

    private static final int AUTH_CODE_LENGTH = 6;


    @Transactional
    public void sendUniversityAuthenticationCode(String clientId,
        MailSendRequestDTO sendDTO) {
        University university = universityRepositoryService.getUniversityByIdOrThrow(
            sendDTO.universityId());

        String mailAddress = sendDTO.mailAddress();
        validateMailAddress(mailAddress, university.getMailPatterns());

        MailAuthentication mailAuthentication = createAndSendMail(clientId,
            mailAddress, university.getName());

        mailAuthenticationRepositoryService.createMailAuthentication(mailAuthentication);
    }

    @Transactional
    public SignInResponseDTO verifyMemberUniversity(String clientId,
        VerifyRequestDTO verifyDTO) {
        University memberUniversity = universityRepositoryService.getUniversityByIdOrThrow(
            verifyDTO.universityId());

        // todo: 출시 전 삭제
        if (verifyDTO.code().equals("cozymate")) {
            return authService.signInByPreMember(clientId, memberUniversity,
                verifyDTO.majorName());
        }

        verifyAuthenticationCode(clientId, verifyDTO.code(), memberUniversity.getMailPatterns());

        return authService.signInByPreMember(clientId, memberUniversity,
            verifyDTO.majorName());
    }

    public String isVerified(Member member) {
        Optional<MailAuthentication> mailAuthentication = mailAuthenticationRepositoryService.getMailAuthenticationByIdOptional(
            member.getClientId());
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

            helper.setTo(ADMIN_MAIL_ADDRESS);
            helper.setSubject(subject);
            helper.setText(content, true); // 전달받은 content를 그대로 전송
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus._MAIL_SEND_FAIL);
        }
    }

    private void verifyAuthenticationCode(String clientId, String requestCode,
        List<String> mailPatterns) {

        MailAuthentication mailAuthentication = mailAuthenticationRepositoryService.getMailAuthenticationByIdOrThrow(
            clientId);
        // 만료 시간 초과 여부 확인
        if (LocalDateTime.now()
            .isAfter(
                mailAuthentication.getUpdatedAt().plusMinutes(MAIL_AUTHENTICATION_EXPIRED_TIME))) {
            throw new GeneralException(ErrorStatus._MAIL_AUTHENTICATION_CODE_EXPIRED);
        }

        // 메일 중복 확인
        validateMailAddress(mailAuthentication.getMailAddress(), mailPatterns);

        // 메일 인증 코드 일치 여부 확인
        if (!mailAuthentication.getCode().equals(requestCode)) {
            throw new GeneralException(ErrorStatus._MAIL_AUTHENTICATION_CODE_INCORRECT);
        }

        mailAuthentication.verify();
    }

    private MailAuthentication createAndSendMail(String clientId, String mailAddress,
        String universityName) {
        if (mailAddress.equals("test123@inha.edu")) {
            return MailConverter.toMailAuthenticationWithParams(clientId, mailAddress, "123456",
                false);
        } // 애플심사를 위한 테스트 메일 인증

        String authenticationCode = Base64.getEncoder()
            .encodeToString(UUID.randomUUID().toString().getBytes())
            .replaceAll(CONFUSION_CHARACTERS_REGEX, "") // 제외할 문자 제거
            .substring(0, AUTH_CODE_LENGTH);

        String emailBody = makeMailBody(authenticationCode, universityName);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            DataSource logo = createLogo();

            helper.setFrom(FROM);
            helper.setTo(mailAddress);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setText(emailBody, true);

            if (logo != null) {
                helper.addInline("logo", logo);
            }

            mailSender.send(message);

            return MailConverter.toMailAuthenticationWithParams(clientId, mailAddress,
                authenticationCode, false);
        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus._MAIL_SEND_FAIL);
        }
    }

    private void validateMailAddress(String mailAddress, List<String> mailPatterns) {
//        String domain = mailAddress.substring(mailAddress.indexOf('@') + 1);
//        if (!mailPatterns.contains(domain)) {
//            throw new GeneralException(ErrorStatus._INVALID_MAIL_ADDRESS_DOMAIN);
//        }
        List<MailAuthentication> mailAuthentications = mailAuthenticationRepositoryService.getMailAuthenticationListByMailAddress(
            mailAddress);

        boolean hasVerified = mailAuthentications.stream()
            .anyMatch(MailAuthentication::getIsVerified);

        if (hasVerified) {
            throw new GeneralException(ErrorStatus._MAIL_ADDRESS_DUPLICATED);
        }
    }

    private String makeMailBody(String authenticationCode, String universityName) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                MAIL_TEMPLATE_PATH);

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

    private DataSource createLogo() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(LOGO_IMAGE_PATH)) {
            if (is == null) {
                return null;
            }

            byte[] imageBytes = is.readAllBytes();
            return new ByteArrayDataSource(imageBytes, "image/png");
        } catch (IOException e) {
            return null;
        }
    }


}