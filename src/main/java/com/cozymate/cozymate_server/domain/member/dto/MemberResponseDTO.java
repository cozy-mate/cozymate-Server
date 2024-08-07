package com.cozymate.cozymate_server.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class MemberResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        private String name;
        private String nickname;
        private String gender;
        private String birthday;
        private Integer persona;
    }

}
