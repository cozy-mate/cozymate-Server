package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.logging.enums.MdcKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpNotificationService {


    @Value("${discord.webhook-url}")
    private String signUpNotificationUri;

    public void sendSignUpNotification(Member member) {
        // WebClient를 사용하여 AWS Lambda로 회원가입 알림을 보냄
        WebClient webClient = WebClient.builder().baseUrl(signUpNotificationUri).build();

        Map<String, String> contentMap = new HashMap<>();

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("### 새로운 유저 등장!\n");
        contentBuilder.append("**")
            .append("Id").append("**: ")
            .append(member.getId()).append("\n");
        contentBuilder.append("**")
            .append("Nickname").append("**: ")
            .append(member.getNickname()).append("\n");
        contentBuilder.append("**")
            .append("Gender").append("**: ")
            .append(member.getGender()).append("\n");
        contentBuilder.append("**")
            .append("SocialType").append("**: ")
            .append(member.getSocialType()).append("\n");
        contentBuilder.append("**")
            .append("Platform").append("**: ")
            .append(MDC.get(MdcKey.REQEUST_AGENT.toString())).append("\n");
        contentBuilder.append("-# ").append(LocalDateTime.now().toString()).append("\n");
        contentMap.put("content", contentBuilder.toString());

        // Non-Blocking 방식으로 호출하고 응답을 기다리지 않음
        webClient.post()
            .bodyValue(contentMap)
            .retrieve()
            .bodyToMono(Void.class)
            .subscribe();

    }

}
