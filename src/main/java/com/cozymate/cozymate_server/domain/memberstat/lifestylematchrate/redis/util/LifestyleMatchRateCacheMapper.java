package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.util;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.LifestyleMatchRateRedisDTO;

public class LifestyleMatchRateCacheMapper {
    public static LifestyleMatchRateRedisDTO toDto(Long memberA, Long memberB, Integer matchRate) {
        return LifestyleMatchRateRedisDTO.builder()
            .memberA(memberA)
            .memberB(memberB)
            .matchRate(matchRate)
            .build();
    }

    public static LifestyleMatchRateRedisDTO toDto(LifestyleMatchRate entity) {
        return LifestyleMatchRateRedisDTO.builder()
            .memberA(entity.getId().getMemberA())
            .memberB(entity.getId().getMemberB())
            .matchRate(entity.getMatchRate())
            .build();
    }

}
