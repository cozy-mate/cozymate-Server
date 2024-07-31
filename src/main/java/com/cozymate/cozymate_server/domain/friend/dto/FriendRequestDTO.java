package com.cozymate.cozymate_server.domain.friend.dto;

import com.cozymate.cozymate_server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
        // 아직은 이 경우 말고 생각해본적이 없어, static class는 만들지 않았습니다.
        Long requesterId;
}
