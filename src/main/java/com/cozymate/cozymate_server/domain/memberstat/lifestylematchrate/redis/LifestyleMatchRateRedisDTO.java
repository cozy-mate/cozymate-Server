package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifestyleMatchRateRedisDTO implements Serializable {

    private Long memberA;
    private Long memberB;
    private Integer matchRate;

    public String generateKey() {
        long first = Math.min(memberA, memberB);
        long second = Math.max(memberA, memberB);
        return "matchRate:" + first + ":" + second;
    }
}

