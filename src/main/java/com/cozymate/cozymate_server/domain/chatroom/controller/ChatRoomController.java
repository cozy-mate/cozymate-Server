package com.cozymate.cozymate_server.domain.chatroom.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto.ChatRoomIdResponse;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto.ChatRoomSimpleDto;
import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomCommandService;
import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomQueryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;
    private final ChatRoomQueryService chatRoomQueryService;

    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "[베로] 쪽지방 삭제 기능", description = "chatRoomId : 나갈 쪽지방 pk")
    @SwaggerApiError({
        ErrorStatus._CHATROOM_NOT_FOUND,
        ErrorStatus._CHATROOM_FORBIDDEN
    })
    public ResponseEntity<ApiResponse<String>> deleteChatRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long chatRoomId) {
        chatRoomCommandService.deleteChatRoom(memberDetails.getMember(), chatRoomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("쪽지방 삭제 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 쪽지방 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getChatRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(chatRoomQueryService.getChatRoomList(memberDetails.getMember())));
    }

    @GetMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지방 반환", description = "")
    @SwaggerApiError(
        ErrorStatus._MEMBER_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<ChatRoomIdResponse>> getChatRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long recipientId) {
        Member member = memberDetails.getMember();

        ChatRoomSimpleDto simpleDto = chatRoomQueryService.getChatRoom(member, recipientId);
        Optional<ChatRoom> chatRoom = simpleDto.getChatRoom();
        if (chatRoom.isPresent()) {
            return ResponseEntity.ok(ApiResponse.onSuccess(
                ChatRoomConverter.toChatRoomIdResponse(chatRoom.get().getId())));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatRoomCommandService.saveChatRoom(member, simpleDto.getRecipient())));
    }
}