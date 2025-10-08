package com.cozymate.cozymate_server.domain.chat.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.service.ChatService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chats")
    public void sendChat(@Payload @Valid CreateChatRequestDTO createChatRequestDTO) {
        chatService.sendChat(createChatRequestDTO);
    }

    @Operation(summary = "[베로] 채팅 전송 (docs)", description = "WebSocket STOMP로 채팅 전송")
    @GetMapping("/pub/chats")
    public ResponseEntity<ApiResponse<ChatPubDTO>> sendChatForSwagger(
        @RequestBody CreateChatRequestDTO createChatRequestDTO) {
        return ResponseEntity.ok(ApiResponse.onSuccess(ChatPubDTO.builder().build()));
    }

    @Operation(summary = "[베로] 과거 채팅 조회", description = "조회된 채팅 중 가장 이전 채팅의 LocalDateTime과 sequence를 파라미터로 넘겨주세요")
    @GetMapping("/chats/chatrooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatListResponseDTO>> getChatList(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long chatRoomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastChatTime,
        @RequestParam Long sequence) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatService.getChatListBeforeLastChatTime(memberDetails.member(), chatRoomId,
                lastChatTime, sequence)));
    }
}
