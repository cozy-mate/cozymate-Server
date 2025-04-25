package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.MemberStatRedisDTO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberStatCacheService {
    private static final String KEY_PREFIX = "memberStat:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(MemberStatRedisDTO dto) {
        String key = getKey(dto.getMemberId());
        redisTemplate.opsForValue().set(key, dto);
        log.info("캐시 저장 완료 - {}", key);
    }

    public Optional<MemberStatRedisDTO> get(Long memberId) {
        String key = getKey(memberId);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof MemberStatRedisDTO dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public void delete(Long memberId) {
        String key = getKey(memberId);
        redisTemplate.delete(key);
        log.info("캐시 삭제 - {}", key);
    }

    public boolean exists(Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getKey(memberId)));
    }

    private String getKey(Long memberId) {
        return KEY_PREFIX + memberId;
    }

}
