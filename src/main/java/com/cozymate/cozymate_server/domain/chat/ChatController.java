package com.cozymate.cozymate_server.domain.chat;

import com.cozymate.cozymate_server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    /**
     * [POST] 쪽지 작성
     * TODO: ChatRequestDto의 senderId는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정
     */
    @PostMapping("/members/{recipientId}")
    public ApiResponse<String> createChat(
        @Valid @RequestBody ChatRequestDto chatRequestDto, @PathVariable Long recipientId) {
        chatService.createChat(chatRequestDto, recipientId);
        return ApiResponse.onSuccess("쪽지 작성 완료");
    }
}