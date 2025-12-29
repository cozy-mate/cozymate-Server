package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.service;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.LifestyleMatchRateRedisDTO;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.util.LifestyleMatchRateCacheMapper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LifestyleMatchRateCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PREFIX = "matchRate:";
    private static final String DELIM = ":";
    private static final String ALL = "*";

    public void save(LifestyleMatchRateRedisDTO dto) {
        String key = generateKey(dto.getMemberA(), dto.getMemberB());
        redisTemplate.opsForValue().set(key, dto);
    }
    public void saveAll(List<LifestyleMatchRateRedisDTO> dtoList) {
        Map<String, Object> keyValueMap = dtoList.stream()
            .collect(Collectors.toMap(
                dto -> generateKey(dto.getMemberA(), dto.getMemberB()),
                dto -> dto
            ));

        redisTemplate.opsForValue().multiSet(keyValueMap);
    }

    public void saveLifeStyleMatchRate(List<LifestyleMatchRate> matchRateList) {
        List<LifestyleMatchRateRedisDTO> dtoList = matchRateList.stream()
            .map(LifestyleMatchRateCacheMapper::toDto)
            .toList();

        saveAll(dtoList); // 일괄 저장
    }


    public Optional<LifestyleMatchRateRedisDTO> find(Long memberA, Long memberB) {
        String key = generateKey(memberA, memberB);
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof LifestyleMatchRateRedisDTO dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Map<Long, Integer> findMatchRates(Long baseUserId, List<Long> candidateIds) {
        List<String> keys = candidateIds.stream()
            .filter(id -> !id.equals(baseUserId))
            .map(id -> generateKey(id, baseUserId))
            .toList();


        List<Object> values = redisTemplate.opsForValue().multiGet(keys);


        Map<Long, Integer> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            Object value = values.get(i);
            if (value instanceof LifestyleMatchRateRedisDTO dto) {
                Long otherId = dto.getMemberA().equals(baseUserId) ? dto.getMemberB() : dto.getMemberA();
                result.put(otherId, dto.getMatchRate());
            }
        }

        return result;
    }

    public void delete(Long memberA, Long memberB) {
        String key = generateKey(memberA, memberB);
        redisTemplate.delete(key);
    }
    public void deleteAllRelatedTo(Long memberId) {
        String pattern1 = PREFIX + memberId + DELIM + ALL;
        String pattern2 = PREFIX + ALL + DELIM + memberId;

        Set<String> keys1 = redisTemplate.keys(pattern1);
        Set<String> keys2 = redisTemplate.keys(pattern2);

        Set<String> allKeys = new HashSet<>();
        if (keys1 != null) allKeys.addAll(keys1);
        if (keys2 != null) allKeys.addAll(keys2);

        if (!allKeys.isEmpty()) {
            redisTemplate.delete(allKeys);
        }
    }

    private String generateKey(Long memberA, Long memberB) {
        long first = Math.min(memberA, memberB);
        long second = Math.max(memberA, memberB);
        return PREFIX + first + DELIM + second;
    }

}