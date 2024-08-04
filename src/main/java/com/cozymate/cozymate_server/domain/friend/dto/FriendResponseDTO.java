package com.cozymate.cozymate_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleFriendResponseDTO {
        private Long memberId;
        private String memberName;
    }

}
