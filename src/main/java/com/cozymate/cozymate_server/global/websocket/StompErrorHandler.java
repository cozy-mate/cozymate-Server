package com.cozymate.cozymate_server.global.websocket;

import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Configuration
@RequiredArgsConstructor
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
        Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
            ErrorStatus._BAD_REQUEST.getCode(), ex.getMessage(), null);

        return errorMessage(accessor, apiResponse);
    }

    private Message<byte[]> errorMessage(StompHeaderAccessor accessor,
        ApiResponse<Object> apiResponse) {
        try {
            String errorMessage = objectMapper.writeValueAsString(apiResponse);
            return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
        } catch (JsonProcessingException e) {
            return MessageBuilder.createMessage(EMPTY_PAYLOAD, accessor.getMessageHeaders());
        }
    }
}
