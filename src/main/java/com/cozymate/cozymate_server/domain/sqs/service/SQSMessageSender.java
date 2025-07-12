package com.cozymate.cozymate_server.domain.sqs.service;

import com.cozymate.cozymate_server.domain.sqs.dto.FcmSQSMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSMessageSender {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper om;

    @Value("${cloud_sqs.aws.sqs.queue-name}")
    private String QUEUE_NAME;

    public void sendMessage(List<FcmSQSMessage> fcmSqsMessageList) {
        String payload;
        try {
            payload = om.writeValueAsString(fcmSqsMessageList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("SQS 전송용 JSON 직렬화 실패", e);
        }

        try {
            SendResult<String> sendResult = sqsTemplate.send(to -> to
                .queue(QUEUE_NAME)
                .payload(payload));
            log.info("SQS 메시지 전송 성공. 메시지 id : {}", sendResult.messageId());
        } catch (Exception e) {
            log.error("SQS 메시지 전송 실패. payload: {}, 이유: {}", payload, e.getMessage(), e);
        }
    }
}
