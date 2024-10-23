package com.cozymate.cozymate_server.domain.chatroom.dto;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private Integer persona;
    private String nickName;
    private String lastContent;
    private Long chatRoomId;
    private Long memberId;

    @Getter
    @Builder
    public static class ChatRoomIdResponse {
        private Long chatRoomId;
    }

    @Getter
    @Builder
    public static class ChatRoomSimpDto {
        private Optional<ChatRoom> chatRoom;
        private Member recipient;

    }
}