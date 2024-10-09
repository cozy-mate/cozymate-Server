package com.cozymate.cozymate_server.domain.memberblock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberBlockResponseDto {

    private Long memberId;
    private String nickname;
}