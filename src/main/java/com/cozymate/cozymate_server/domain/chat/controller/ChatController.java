package com.cozymate.cozymate_server.domain.chat.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto.ChatSuccessResponseDto;
import com.cozymate.cozymate_server.domain.chat.service.ChatCommandService;
import com.cozymate.cozymate_server.domain.chat.service.ChatQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ChatQueryService chatQueryService;

    @PostMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지 작성 기능", description = "recipientId: 쪽지를 받을 멤버의 pk값, RequestBody의 content: 쪽지 내용")
    @SwaggerApiError({
            ErrorStatus._CHAT_NOT_FOUND_RECIPIENT,
            ErrorStatus._REQUEST_TO_BLOCKED_MEMBER
    })
    public ResponseEntity<ApiResponse<ChatSuccessResponseDto>> createChat(
        @Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long recipientId,
        @AuthenticationPrincipal
        MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatCommandService.createChat(chatRequestDto, memberDetails.getMember(),
                recipientId)));
    }

    @GetMapping("/chatrooms/{chatRoomId}")
    @Operation(summary = "[베로] 쪽지방의 쪽지 상세 내역 조회", description = "chatRoomId : 조회할 쪽지방 pk값")
    @SwaggerApiError({
        ErrorStatus._CHATROOM_NOT_FOUND,
        ErrorStatus._CHATROOM_MEMBER_MISMATCH,
        ErrorStatus._REQUEST_TO_BLOCKED_MEMBER
    })
    public ResponseEntity<ApiResponse<ChatResponseDto>> getChatList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long chatRoomId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                chatQueryService.getChatList(memberDetails.getMember(), chatRoomId)));
    }
}