package com.cozymate.cozymate_server.domain.sqs.service;

import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepositoryService;
import com.cozymate.cozymate_server.domain.sqs.dto.DlqMessage;
import com.cozymate.cozymate_server.domain.sqs.dto.DlqMessage.FailedPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSDlqListener {

    private final ObjectMapper om;
    private final FcmRepositoryService fcmRepositoryService;

    @SqsListener(value = "${cloud_sqs.aws.dlq.queue-name}", factory = "defaultSqsListenerContainerFactory")
    public void messageListener(String message) {
        log.info("리스너 동작");
        try {
            DlqMessage dlqMessage = om.readValue(message, DlqMessage.class);
            List<FailedPayload> failedFcmList = dlqMessage.failedPayloads();
            log.info("실패 메시지 개수: {}", failedFcmList.size());

            List<String> fcmTokenList = failedFcmList.stream()
                .map(f -> f.deviceToken())
                .toList();

            // 실패 토큰 valid false 처리
            fcmRepositoryService.updateFcmValidToFalseByTokenList(fcmTokenList);

        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패 - 원본 메시지: {}", message, e);
        }
    }
}
