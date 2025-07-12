package com.cozymate.cozymate_server.domain.sqs.dto;

import java.util.List;

public record DlqMessage(

    List<FailedPayload> failedPayloads
) {
    public record FailedPayload(

        String deviceToken
    ) {
    }
}