package com.cozymate.cozymate_server.domain.chatroom.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.CountChatRoomsWithNewChatDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomCommandService;
import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomQueryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
import org.springframework.web.bind.annotation.RequestParam;
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
        chatRoomCommandService.deleteChatRoom(memberDetails.member(), chatRoomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("쪽지방 삭제 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 쪽지방 목록 조회 (수정 - 25.03.26)", description = "")
    public ResponseEntity<ApiResponse<PageResponseDto<List<ChatRoomDetailResponseDTO>>>> getChatRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(chatRoomQueryService.getChatRoomList(memberDetails.member(), page, size)));
    }

    @GetMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지방 반환", description = "")
    @SwaggerApiError(
        ErrorStatus._MEMBER_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<ChatRoomIdResponseDTO>> getChatRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long recipientId) {
        Member member = memberDetails.member();

        ChatRoomSimpleDTO simpleDTO = chatRoomQueryService.getChatRoom(member, recipientId);
        Optional<ChatRoom> chatRoom = simpleDTO.chatRoom();
        if (chatRoom.isPresent()) {
            return ResponseEntity.ok(ApiResponse.onSuccess(
                ChatRoomConverter.toChatRoomIdResponseDTO(chatRoom.get().getId())));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatRoomCommandService.saveChatRoom(member, simpleDTO.recipient())));
    }

    @GetMapping("/count/new-chat")
    @Operation(summary = "[베로] 새로운 쪽지가 온 쪽지방의 갯수 반환", description = "")
    @SwaggerApiError(
        ErrorStatus._MEMBER_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<CountChatRoomsWithNewChatDTO>> getCountChatRoomsWithNewChat(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            chatRoomQueryService.countChatRoomsWithNewChat(memberDetails.member())));
    }
}