package com.cozymate.cozymate_server.domain.chat.controller;

import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto;
import com.cozymate.cozymate_server.domain.chat.service.ChatCommandService;
import com.cozymate.cozymate_server.domain.chat.service.ChatQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    // TODO: ChatRequestDto의 senderId는 추후 시큐리티 인증 객체에서 받아 오는 것으로 변경 예정
    @PostMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지 작성 기능", description = "recipientId: 쪽지를 받을 멤버의 pk값, RequestBody의 content: 쪽지 내용")
    public ResponseEntity<ApiResponse<String>> createChat(
        @Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long recipientId) {
        chatCommandService.createChat(chatRequestDto, recipientId);
        return ResponseEntity.ok(ApiResponse.onSuccess("쪽지 작성 완료"));
    }

    // TODO: memberId는 추후 시큐리티 인증 객체에서 받아 오는 것으로 변경 예정
    @GetMapping("/chatrooms/{chatRoomId}")
    @Operation(summary = "[베로] 쪽지방의 쪽지 상세 내역 조회", description = "내 memberId, chatRoomId : 조회할 쪽지방 pk값")
    public ResponseEntity<List<ChatResponseDto>> getChatList(@RequestParam Long memberId,
        @PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatQueryService.getChatList(memberId, chatRoomId));
    }
}