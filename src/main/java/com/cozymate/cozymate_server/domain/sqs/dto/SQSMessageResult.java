package com.cozymate.cozymate_server.domain.sqs.dto;

import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import java.util.List;
import lombok.Builder;

@Builder
public record SQSMessageResult(
    List<FcmSQSMessage> fcmSQSMessageList,
    NotificationLog notificationLog
) {
}
