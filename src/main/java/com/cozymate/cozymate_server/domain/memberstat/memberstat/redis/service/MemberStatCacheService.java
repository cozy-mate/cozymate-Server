package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.service.LifestyleMatchRateCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util.MemberStatExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final LifestyleMatchRateCacheService lifestyleMatchRateCacheService;

    private static final String POOL_KEY_PREFIX = "pool:university:";
    private static final String LIFESTYLE_KEY_PREFIX = "lifestyle:university:";
    private static final String GENDER_KEY = ":gender:";
    private static final String LIFESTYLE_DELIMITER = ":";

    /**
     * MemberStat 기반으로 Pool + Lifestyle Set 전체 등록
     */
    public void save(MemberStat memberStat) {
        Long universityId = memberStat.getMember().getUniversity().getId();
        String gender = memberStat.getMember().getGender().toString();
        Long userId = memberStat.getMember().getId();

        // 1. Pool 등록
        addToMemberPool(universityId, gender, userId);

        // 2. Lifestyle + UniversityStat 필드 등록
        Map<String, String> extractedAnswers = MemberStatExtractor.extractAnswers(
            memberStat);
        for (Map.Entry<String, String> entry : extractedAnswers.entrySet()) {
            if (!entry.getValue().isBlank()) {
                addToLifestyle(universityId, entry.getKey(), entry.getValue(), userId);
            }
        }
    }

    /**
     * MemberStat 기존 거, 바뀐 거 기반으로 Lifestyle Set 수정
     */
    public void update(Member member, MemberStat oldStat, MemberStat newStat) {
        // 1. 라이프스타일 항목 추출
        Map<String, String> oldAnswers = MemberStatExtractor.extractAnswers(oldStat);
        Map<String, String> newAnswers = MemberStatExtractor.extractAnswers(newStat);

        // 2. 변경된 항목만 SREM & SADD
        for (Map.Entry<String, String> entry : newAnswers.entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();
            String oldValue = oldAnswers.get(key);

            // 값이 null → "" 되지 않게 체크
            if (oldValue == null) {
                oldValue = "";
            }
            if (newValue == null) {
                newValue = "";
            }

            if (!newValue.isBlank()) {
                if (!oldValue.equals(newValue)) {
                    // 1) 이전 값 제거
                    if (!oldValue.isBlank()) {
                        String oldLifestyleKey = generateLifestyleKey(
                            member.getUniversity().getId(), key, oldValue);
                        redisTemplate.opsForSet()
                            .remove(oldLifestyleKey, member.getId().toString());
                    }

                    // 2) 새로운 값 추가
                    String newLifestyleKey = generateLifestyleKey(member.getUniversity().getId(),
                        key, newValue);
                    redisTemplate.opsForSet().add(newLifestyleKey, member.getId().toString());
                }
            }
        }

    }

    /**
     * 사용자 + attributeList 기반 필터 검색 (matchRate 내림차순 정렬된 Map 반환)
     */
    public Map<Long, Integer> filterUsersWithAttributeList(
        Long universityId,
        String gender,
        MemberStat memberStat,
        List<String> filterList
    ) {
        // 1. 필터 조건 매핑
        Map<String, List<?>> filterMap = MemberStatExtractor.toFilterMap(memberStat,
            filterList);

        // 2. Redis 조건별 Set 교집합으로 필터링
        List<Long> filteredUserList = filterUsers(universityId, gender, filterMap);

        // 3. matchRate 조회
        Long currentUserId = memberStat.getMember().getId();
        Map<Long, Integer> idToMatchRate = lifestyleMatchRateCacheService.findMatchRates(
            currentUserId, filteredUserList);

        // 4. matchRate 내림차순 정렬된 Map 생성 (LinkedHashMap으로 순서 보존)
        return idToMatchRate.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new // 순서 유지
            ));
    }

    /**
     * 필터 Map<String, List<?>> 기반으로 사용자 필터링
     */
    private List<Long> filterUsers(Long universityId, String gender, Map<String, List<?>> filters) {
        List<String> keys = new ArrayList<>();
        keys.add(generatePoolKey(universityId, gender)); // 기본 Pool 추가

        for (Map.Entry<String, List<?>> entry : filters.entrySet()) {
            for (Object value : entry.getValue()) {
                if (value != null) {
                    keys.add(generateLifestyleKey(universityId, entry.getKey(), value.toString()));
                }
            }
        }

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Object> intersected = redisTemplate.opsForSet().intersect(
            keys.get(0), keys.subList(1, keys.size()));

        if (intersected == null || intersected.isEmpty()) {
            return Collections.emptyList();
        }

        return intersected.stream()
            .map(Object::toString)
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }


    private String generatePoolKey(Long universityId, String gender) {
        return POOL_KEY_PREFIX + universityId + GENDER_KEY + gender;
    }

    private String generateLifestyleKey(Long universityId, String questionKey, String answerValue) {
        return LIFESTYLE_KEY_PREFIX + universityId + LIFESTYLE_DELIMITER + questionKey
            + LIFESTYLE_DELIMITER + answerValue;
    }

    /**
     * 기본 Pool (학교 + 성별)에 사용자 추가
     */
    private void addToMemberPool(Long universityId, String gender, Long userId) {
        String poolKey = generatePoolKey(universityId, gender);
        redisTemplate.opsForSet().add(poolKey, userId.toString());
    }

    /**
     * Lifestyle 조건별 Set에 사용자 추가
     */
    private void addToLifestyle(Long universityId, String questionKey, String answerValue,
        Long userId) {
        String lifestyleKey = generateLifestyleKey(universityId, questionKey, answerValue);
        redisTemplate.opsForSet().add(lifestyleKey, userId.toString());
    }

}
