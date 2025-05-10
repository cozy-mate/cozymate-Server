package com.cozymate.cozymate_server.domain.mail.service;


import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.converter.MailConverter;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;
import com.cozymate.cozymate_server.domain.mail.repository.MailRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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

    private static final Integer MAIL_AUTHENTICATION_EXPIRED_TIME = 30;
    private final JavaMailSender mailSender;
    private final MailRepository mailRepository;
    private final UniversityRepositoryService universityRepositoryService;
    private final AuthService authService;


    private static final String ADMIN_MAIL_ADDRESS = "cozymate0@gmail.com"; // 관리자 이메일 주소

    private static final String CONFUSION_CHARACTERS = "[IlOo0]";


    @Transactional
    public void sendUniversityAuthenticationCode(String clientId,
        MailSendRequestDTO sendDTO) {
        University university = universityRepositoryService.getUniversityByIdOrThrow(
            sendDTO.universityId());

        String mailAddress = sendDTO.mailAddress();
        validateMailAddress(mailAddress, university.getMailPattern());
        deleteDuplicatedAddress(mailAddress);

        MailAuthentication mailAuthentication = createAndSendMail(clientId,
            mailAddress, university.getName());

        mailRepository.save(mailAuthentication);
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

        verifyAuthenticationCode(clientId, verifyDTO.code());

        return authService.signInByPreMember(clientId, memberUniversity,
            verifyDTO.majorName());
    }

    public String isVerified(Member member) {
        Optional<MailAuthentication> mailAuthentication = mailRepository.findById(
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

    private void verifyAuthenticationCode(String clientId, String requestCode) {

        MailAuthentication mailAuthentication = mailRepository.findById(clientId)
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

    private MailAuthentication createAndSendMail(String clientId, String mailAddress,
        String universityName) {
        if (mailAddress.equals("test123@inha.edu")) {
            return MailConverter.toMailAuthenticationWithParams(clientId, mailAddress, "123456",
                false);
        } // 애플심사를 위한 테스트 메일 인증

        String authenticationCode = Base64.getEncoder()
            .encodeToString(UUID.randomUUID().toString().getBytes())
            .replaceAll(CONFUSION_CHARACTERS, "") // 제외할 문자 제거
            .substring(0, 6);

        String emailBody = makeMailBody(authenticationCode, universityName);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(mailAddress);
            helper.setSubject("cozymate 대학교 메일인증");
            helper.setText(emailBody, true);
            mailSender.send(message);

            return MailConverter.toMailAuthenticationWithParams(clientId, mailAddress,
                authenticationCode, false);
        } catch (MessagingException e) {
            throw new GeneralException(ErrorStatus._MAIL_SEND_FAIL);
        }
    }

    private void validateMailAddress(String mailAddress, String mailPattern) {
        if (!mailAddress.contains(mailPattern)) {
            throw new GeneralException(ErrorStatus._INVALID_MAIL_ADDRESS_DOMAIN);
        }
        List<MailAuthentication> mailAuthentications = mailRepository.findAllByMailAddress(
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

    private void deleteDuplicatedAddress(String mailAddress) {
        // 같은 mailAddress로 인증 안 된 기록 모두 삭제
        List<MailAuthentication> existing = mailRepository.findAllByMailAddress(mailAddress);
        existing.stream()
            .filter(auth -> Boolean.FALSE.equals(auth.getIsVerified()))
            .forEach(mailRepository::delete);

    }

}