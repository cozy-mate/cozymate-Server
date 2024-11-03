package com.cozymate.cozymate_server.domain.fcm.dto;

import com.google.firebase.messaging.Message;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageResult {

    private Map<Message, String> messageTokenMap;
    private List<Message> messages;
    private String content;
}