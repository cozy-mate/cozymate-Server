package com.cozymate.cozymate_server.domain.friend.converter;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendLikeResponseDTO;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendSummaryResponseDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.Member;

public class FriendConverter {

    public static Friend toEntity(Member sender, Member receiver) {
        return Friend.builder()
            .sender(sender)
            .receiver(receiver)
            .status(FriendStatus.WAITING)
            .likesSender(Boolean.FALSE)
            .likesReceiver(Boolean.FALSE)
            .build();
    }

    public static FriendSummaryResponseDTO toFriendSummaryResponseDTO(Member member, Boolean like) {
        return FriendSummaryResponseDTO.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .like(like)
            .build();
    }

    public static FriendLikeResponseDTO toFriendLikeResponseDTO(Member member, Boolean like) {
        return FriendLikeResponseDTO.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .like(like)
            .build();
    }

}
