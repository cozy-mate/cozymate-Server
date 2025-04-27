package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.service;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.LifestyleMatchRateRedisDTO;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.util.LifestyleMatchRateCacheMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LifestyleMatchRateCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "matchRate:";

    public void save(LifestyleMatchRateRedisDTO dto) {
        String key = generateKey(dto.getMemberA(), dto.getMemberB());
        redisTemplate.opsForValue().set(key, dto);
    }

    public void saveLifeStyleMatchRate(List<LifestyleMatchRate> matchRateList) {
        matchRateList.forEach(entity -> {
            LifestyleMatchRateRedisDTO dto = LifestyleMatchRateCacheMapper.toDto(entity);
            save(dto);
        });
    }

    public Optional<LifestyleMatchRateRedisDTO> find(Long memberA, Long memberB) {
        String key = generateKey(memberA, memberB);
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof LifestyleMatchRateRedisDTO dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public void delete(Long memberA, Long memberB) {
        String key = generateKey(memberA, memberB);
        redisTemplate.delete(key);
    }

    private String generateKey(Long memberA, Long memberB) {
        long first = Math.min(memberA, memberB);
        long second = Math.max(memberA, memberB);
        return PREFIX + first + ":" + second;
    }
}