package com.cozymate.cozymate_server.domain.chatroom.dto;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import lombok.Builder;

@Builder
public record ChatRoomSimpleDTO(

    Optional<ChatRoom> chatRoom,
    Member recipient
) {

}