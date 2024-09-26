package com.cozymate.cozymate_server.domain.mail.service;


import com.cozymate.cozymate_server.domain.mail.dto.MailResponseDTO;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final Map<String, String> memberAuthenticationCodeMap = new HashMap<>();
    private final JavaMailSender mailSender;

    public void sendUniversityAuthenticationCode(String clientId, String mailAddress) {
        SimpleMailMessage message = new SimpleMailMessage();
        String authCode = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()).substring(0,6);
        message.setTo(mailAddress);
        message.setSubject("COZYMATE 대학교 메일인증");
        message.setText("COZYMATE 대학교 메일인증 코드입니다 : " + authCode);
        mailSender.send(message);

        memberAuthenticationCodeMap.put(clientId, authCode);

        log.info("auth code : {}", authCode);
        log.info("member : {}", clientId);
        log.info("email : {}", mailAddress);
    }

    public MailResponseDTO.verifyResponseDTO verifyAuthenticationCode(String clientId, String authenticationCode) {
        if (memberAuthenticationCodeMap.get(clientId).equals(authenticationCode)){
            return MailResponseDTO.verifyResponseDTO.builder()
                    .isVerified(true)
                    .message("성공입니다")
                    .build();
        }
        return MailResponseDTO.verifyResponseDTO.builder()
                .isVerified(false)
                .message("인증 코드가 일치하지 않습니다")
                .build();
    }

}
