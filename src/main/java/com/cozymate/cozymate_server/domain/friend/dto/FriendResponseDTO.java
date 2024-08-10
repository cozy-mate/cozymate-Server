package com.cozymate.cozymate_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FriendResponseDTO {

    //Summary와 Detail로 사용하겠습니다.
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FriendSummaryResponseDTO {

        private Long memberId;
        private String nickname;
        private Boolean like;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FriendLikeResponseDTO {

        private Long memberId;
        private String nickname;
        private Boolean like;

    }

}
