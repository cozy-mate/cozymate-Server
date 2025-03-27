package com.cozymate.cozymate_server.domain.chat.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.service.ChatCommandService;
import com.cozymate.cozymate_server.domain.chat.service.ChatQueryService;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지 작성 기능", description = "recipientId: 쪽지를 받을 멤버의 pk값, RequestBody의 content: 쪽지 내용")
    @SwaggerApiError({
        ErrorStatus._CHAT_NOT_FOUND_RECIPIENT
    })
    public ResponseEntity<ApiResponse<ChatRoomIdResponseDTO>> createChat(
        @Valid @RequestBody CreateChatRequestDTO createChatRequestDTO,
        @PathVariable Long recipientId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatCommandService.createChat(createChatRequestDTO, memberDetails.member(),
                recipientId)));
    }

    @GetMapping("/chatrooms/{chatRoomId}")
    @Operation(summary = "[베로] 쪽지방의 쪽지 상세 내역 조회 (수정 - 25.03.26)", description = "chatRoomId : 조회할 쪽지방 pk값")
    @SwaggerApiError({
        ErrorStatus._CHATROOM_NOT_FOUND,
        ErrorStatus._CHATROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL,
        ErrorStatus._CHATROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL,
        ErrorStatus._CHATROOM_INVALID_MEMBER
    })
    public ResponseEntity<ApiResponse<PageResponseDto<ChatListResponseDTO>>> getChatList(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long chatRoomId,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                chatQueryService.getChatList(memberDetails.member(), chatRoomId, page, size)));
    }
}