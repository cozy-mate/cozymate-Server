package com.cozymate.cozymate_server.domain.sqs.dto;

import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record SQSMessageResult(
    Map<FcmSQSMessage, String> messageTokenMap,
    List<FcmSQSMessage> fcmSQSMessageList,
    NotificationLog notificationLog
) {
}
