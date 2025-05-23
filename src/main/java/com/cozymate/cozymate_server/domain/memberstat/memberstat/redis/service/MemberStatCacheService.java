package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.service.LifestyleMatchRateCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util.MemberStatExtractor;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 사용자 Pool 및 Lifestyle 기반 Redis 캐시 관리 서비스
 */
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

    private static final Set<String> MULTI_VALUE_QUESTION = Set.of("personalities", "sleepingHabits");

    /**
     * 사용자의 MemberStat 기반 Redis 캐시 전체 초기 등록
     * - Pool + Lifestyle + UniversityStat 항목을 기준으로 Set에 삽입
     */
    public void save(MemberStat memberStat) {
        Long universityId = memberStat.getMember().getUniversity().getId();
        String gender = memberStat.getMember().getGender().toString();
        Long userId = memberStat.getMember().getId();

        addToMemberPool(universityId, gender, userId);

        Map<String, String> extractedAnswers = MemberStatExtractor.extractAnswers(memberStat);
        for (Map.Entry<String, String> entry : extractedAnswers.entrySet()) {
            if (entry.getValue().isBlank()) {
                throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
            }
            if (MULTI_VALUE_QUESTION.contains(entry.getKey())) {
                addMultiValues(universityId, entry.getKey(), entry.getValue(), userId);
                continue;
            }
            addToLifestyle(universityId, entry.getKey(), entry.getValue(), userId);
        }
    }

    /**
     * 기존 MemberStat과 변경된 MemberStat 비교 기반 캐시 수정 (SREM/SADD)
     */
    public void update(Member member, MemberStat oldStat, MemberStat newStat) {
        Map<String, String> oldAnswers = MemberStatExtractor.extractAnswers(oldStat);
        Map<String, String> newAnswers = MemberStatExtractor.extractAnswers(newStat);

        for (Map.Entry<String, String> entry : newAnswers.entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();
            String oldValue = oldAnswers.get(key);

            if (newValue == null || newValue.isBlank()) {
                throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
            }

            if (oldValue.equals(newValue)) {
                continue;
            }

            if (MULTI_VALUE_QUESTION.contains(key)) {
                removeMultiValues(member.getUniversity().getId(), key, oldValue, newValue, member.getId());
                addMultiValues(member.getUniversity().getId(), key, newValue, member.getId());
                continue;
            }

            removeLifestyle(member.getUniversity().getId(), key, oldValue, member.getId());
            addToLifestyle(member.getUniversity().getId(), key, newValue, member.getId());
        }
    }

    /**
     * 필터 조건 기반 사용자 ID → 매칭률 Map 반환 (정렬 포함)
     */
    public Map<Long, Integer> filterUsersWithAttributeList(Long universityId, String gender, MemberStat memberStat, List<String> filterList) {
        Map<String, List<?>> filterMap = MemberStatExtractor.toFilterMap(memberStat, filterList);
        List<Long> filteredUserList = filterUsers(universityId, gender, filterMap);

        return lifestyleMatchRateCacheService.findMatchRates(memberStat.getMember().getId(), filteredUserList)
            .entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
    }

    public void delete(MemberStat memberStat) {
        Long universityId = memberStat.getMember().getUniversity().getId();
        String gender = memberStat.getMember().getGender().toString();
        Long memberId = memberStat.getMember().getId();

        // 1. 기본 풀에서 제거
        String poolKey = generatePoolKey(universityId, gender);
        redisTemplate.opsForSet().remove(poolKey, memberId.toString());

        // 2. 라이프스타일 Set에서 제거
        Map<String, String> extractedAnswers = MemberStatExtractor.extractAnswers(memberStat);
        for (Map.Entry<String, String> entry : extractedAnswers.entrySet()) {
            String question = entry.getKey();
            String answer = entry.getValue();

            if (answer.isBlank()) continue;

            if (MULTI_VALUE_QUESTION.contains(question)) {
                for (String val : answer.split(",")) {
                    if (!val.isBlank()) {
                        String lifestyleKey = generateLifestyleKey(universityId, question, val.trim());
                        redisTemplate.opsForSet().remove(lifestyleKey, memberId.toString());
                    }
                }
            } else {
                String lifestyleKey = generateLifestyleKey(universityId, question, answer);
                redisTemplate.opsForSet().remove(lifestyleKey, memberId.toString());
            }
        }

        // 3. 매칭률 캐시도 삭제
        lifestyleMatchRateCacheService.deleteAllRelatedTo(memberId);
    }


    /**
     * 필터 조건 기반 사용자 ID 리스트 필터링
     * - 각 조건별 Union 후, 전체 조건에 대해 교집합 수행
     */
    private List<Long> filterUsers(Long universityId, String gender, Map<String, List<?>> filters) {
        List<Set<Object>> groupedKeyResults = new ArrayList<>();

        Set<Object> basePool = getBasePool(universityId, gender);
        if (basePool == null || basePool.isEmpty()) return Collections.emptyList();
        groupedKeyResults.add(basePool);

        for (Map.Entry<String, List<?>> entry : filters.entrySet()) {
            Set<Object> unioned = getUnionedSetPerFilter(universityId, entry.getKey(), entry.getValue());
            if (unioned == null || unioned.isEmpty()) return Collections.emptyList();
            groupedKeyResults.add(unioned);
        }

        Set<Object> result = intersectGroupedResults(groupedKeyResults);

        return result.stream().map(Object::toString).map(Long::valueOf).toList();
    }

    /** Pool Key 기반 사용자 Set 조회 */
    private Set<Object> getBasePool(Long universityId, String gender) {
        String poolKey = generatePoolKey(universityId, gender);
        return redisTemplate.opsForSet().members(poolKey);
    }

    /** 필터 조건 키 목록을 기반으로 Redis Set Union 결과 반환 */
    private Set<Object> getUnionedSetPerFilter(Long universityId, String key, List<?> values) {
        List<String> keys = values.stream()
            .filter(Objects::nonNull)
            .map(val -> generateLifestyleKey(universityId, key, val.toString()))
            .toList();

        if (keys.isEmpty()) return null;
        return redisTemplate.opsForSet().union(keys.get(0), keys.subList(1, keys.size()));
    }

    /** Set 목록 간 메모리 교집합 수행 */
    private Set<Object> intersectGroupedResults(List<Set<Object>> sets) {
        Set<Object> result = sets.get(0);
        for (int i = 1; i < sets.size(); i++) {
            result.retainAll(sets.get(i));
            if (result.isEmpty()) break;
        }
        return result;
    }

    /** 기본 Pool Key 생성 */
    private String generatePoolKey(Long universityId, String gender) {
        return POOL_KEY_PREFIX + universityId + GENDER_KEY + gender;
    }

    /** Lifestyle Set Key 생성 */
    private String generateLifestyleKey(Long universityId, String questionKey, String answerValue) {
        return LIFESTYLE_KEY_PREFIX + universityId + LIFESTYLE_DELIMITER + questionKey + LIFESTYLE_DELIMITER + answerValue;
    }

    /** Pool Set에 사용자 추가 */
    private void addToMemberPool(Long universityId, String gender, Long userId) {
        String poolKey = generatePoolKey(universityId, gender);
        redisTemplate.opsForSet().add(poolKey, userId.toString());
    }

    /** Lifestyle 항목별 Set에 사용자 추가 */
    private void addToLifestyle(Long universityId, String questionKey, String answerValue, Long userId) {
        String lifestyleKey = generateLifestyleKey(universityId, questionKey, answerValue);
        redisTemplate.opsForSet().add(lifestyleKey, userId.toString());
    }

    /** 복수 값 항목 (bitmask) 분해 후 Set에 추가 */
    private void addMultiValues(Long universityId, String questionKey, String answerValues, Long userId) {
        for (String v : answerValues.split(",")) {
            if (!v.isBlank()) {
                addToLifestyle(universityId, questionKey, v.trim(), userId);
            }
        }
    }

    /** Lifestyle 항목 Set에서 사용자 제거 */
    private void removeLifestyle(Long universityId, String questionKey, String answerValue, Long userId) {
        String keyToRemove = generateLifestyleKey(universityId, questionKey, answerValue);
        redisTemplate.opsForSet().remove(keyToRemove, userId.toString());
    }

    /** 복수 값 항목 수정 시 이전 값들 중 제거할 항목 삭제 */
    private void removeMultiValues(Long universityId, String questionKey, String oldValue, String newValue, Long userId) {
        Set<String> oldSet = Set.of(oldValue.split(","));
        Set<String> newSet = Set.of(newValue.split(","));

        for (String val : oldSet) {
            if (!val.isBlank() && !newSet.contains(val)) {
                removeLifestyle(universityId, questionKey, val.trim(), userId);
            }
        }
    }
}

