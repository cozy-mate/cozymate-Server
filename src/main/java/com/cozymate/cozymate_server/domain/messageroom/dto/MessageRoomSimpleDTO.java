package com.cozymate.cozymate_server.domain.messageroom.dto;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MessageRoomSimpleDTO(

    Optional<MessageRoom> messageRoom,
    Member recipient
) {

}