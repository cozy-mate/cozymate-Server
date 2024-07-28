package com.cozymate.cozymate_server.domain.chatroom.controller;

import com.cozymate.cozymate_server.domain.chatroom.service.ChatRoomCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;

    // TODO: 파라미터로 받는 myId는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정
    @DeleteMapping("/{chatRoomId}")
    @Operation(summary = "[베로] 쪽지방 삭제 기능", description = "chatRoomId : 나갈 쪽지방 pk")
    public ResponseEntity<ApiResponse<String>> deleteChatRoom(
        @RequestParam Long myId, @PathVariable Long chatRoomId) {
        chatRoomCommandService.deleteChatRoom(myId, chatRoomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("쪽지방 삭제 완료"));
    }
}