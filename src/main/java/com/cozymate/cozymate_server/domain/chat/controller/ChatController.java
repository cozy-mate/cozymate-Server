package com.cozymate.cozymate_server.domain.chat.controller;

import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chat.service.ChatCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatCommandService chatCommandService;

    // TODO: ChatRequestDto의 senderId는 추후 시큐리티 인증 객체에서 받아 오는 것으로 변경 예정
    @PostMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지 작성 기능", description = "recipientId: 쪽지를 받을 멤버의 pk값, RequestBody의 content: 쪽지 내용")
    public ResponseEntity<String> createChat(
        @Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long recipientId) {
        chatCommandService.createChat(chatRequestDto, recipientId);
        return ResponseEntity.ok().body("쪽지 작성 완료");
    }
}