package com.cozymate.cozymate_server.domain.chatroom.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.service.ChatService;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @Operation(summary = "[베로] 채팅방 목록 조회", description = "소속 대학의 모든 기숙사 채팅방을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDTO>>> getChatRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(chatRoomService.getChatRoomList(memberDetails.member())));
    }

    @Operation(summary = "[베로] 채팅방 입장", description = "채팅방의 최근 20개 채팅을 return 합니다.")
    @PostMapping("/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatListResponseDTO>> enterChatRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long chatRoomId) {
        ChatRoomMember chatRoomMember = chatRoomService.enterChatRoom(memberDetails.member(),
            chatRoomId);

        return ResponseEntity.ok(
            ApiResponse.onSuccess(chatService.getRecentChatList(chatRoomMember)));
    }

    @Operation(summary = "[베로] 채팅방 알림 수신 여부 수정", description = "알림 켜기 -> notificationEnabled : true, 알림 끄기 -> notificationEnabled : false")
    @PatchMapping("/{chatRoomId}/notification")
    public ResponseEntity<ApiResponse<String>> updateNotificationStatus(@AuthenticationPrincipal
    MemberDetails memberDetails, @PathVariable Long chatRoomId, @RequestParam boolean notificationEnabled) {
        chatRoomService.updateNotificationEnabled(memberDetails.member(), chatRoomId,
            notificationEnabled);
        return ResponseEntity.ok(ApiResponse.onSuccess("업데이트 완료"));
    }
}
