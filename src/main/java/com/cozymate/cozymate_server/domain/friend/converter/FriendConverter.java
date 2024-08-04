package com.cozymate.cozymate_server.domain.friend.converter;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.dto.FriendRequestDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.Member;

public class FriendConverter {

    public static Friend toEntity(Member sender, Member receiver) {
        return Friend.builder()
            .sender(sender)
            .receiver(receiver)
            .status(FriendStatus.WAITING)
            .build();
    }

}