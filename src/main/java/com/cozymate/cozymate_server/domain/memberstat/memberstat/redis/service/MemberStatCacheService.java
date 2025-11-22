package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.service;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.redis.service.LifestyleMatchRateCacheService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.DeleteCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.SaveCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command.UpdateCommand;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util.MemberStatExtractor;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/**
 * 사용자 Pool 및 Lifestyle 기반 Redis 캐시 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatCacheService {

    private final StringRedisTemplate redis;
    private final LifestyleMatchRateCacheService lifestyleMatchRateCacheService; // ← 필드 참조

    private static final String POOL_PREFIX = "pool:university:%s:gender:%s";
    private static final String LIFESTYLE_PREFIX = "lifestyle:university:%s:%s:%s";
    private static final String HASROOM_KEY = "pool:university:%s:gender:%s:hasroom";
    private static final Set<String> MULTI_VALUE_QUESTION = Set.of("personalities",
        "sleepingHabits");

    private DefaultRedisScript<Long> upsertScript;  // SADD pool + removes + adds
    private DefaultRedisScript<Long> deleteScript;  // SREM pool + SREM lifestyles

    @PostConstruct
    void initScripts() {
        String upsert =
            "local uid = ARGV[1]\n" +
                "local remN = tonumber(ARGV[2])\n" +
                "local addN = tonumber(ARGV[3])\n" +
                "redis.call('SADD', KEYS[1], uid)\n" +
                "for i=1,remN do redis.call('SREM', KEYS[1+i], uid) end\n" +
                "for i=1,addN do redis.call('SADD', KEYS[1+remN+i], uid) end\n" +
                "return 1\n";
        upsertScript = new DefaultRedisScript<>(upsert, Long.class);

        String del =
            "local uid = ARGV[1]\n" +
                "redis.call('SREM', KEYS[1], uid)\n" +
                "for i=2,#KEYS do redis.call('SREM', KEYS[i], uid) end\n" +
                "return 1\n";
        deleteScript = new DefaultRedisScript<>(del, Long.class);
    }


    public void saveByArgs(
        SaveCommand command) {
        String poolKey = poolKey(command.universityId(), command.gender());
        List<String> addKeys = expandLifestyleKeys(command.universityId(), command.answers());
        List<String> keys = new ArrayList<>(1 + addKeys.size());
        keys.add(poolKey);
        keys.addAll(addKeys);

        redis.execute(upsertScript, keys,
            encodeMemberId(command.memberId()),
            "0",
            String.valueOf(addKeys.size()));
    }

    public void updateByArgs(UpdateCommand command) {
        String poolKey = poolKey(command.universityId(), command.gender());
        Diff diff = diffKeys(command.universityId(), command.oldAnswers(), command.newAnswers());

        List<String> keys = new ArrayList<>(1 + diff.removes().size() + diff.adds().size());
        keys.add(poolKey);
        keys.addAll(diff.removes());
        keys.addAll(diff.adds());

        redis.execute(upsertScript, keys,
            encodeMemberId(command.memberId()),
            String.valueOf(diff.removes().size()),
            String.valueOf(diff.adds().size()));
    }

    public void deleteByArgs(DeleteCommand command) {
        String poolKey = poolKey(command.universityId(), command.gender());
        List<String> lifestyleKeys = expandLifestyleKeys(command.universityId(), command.answers());

        List<String> keys = new ArrayList<>(1 + lifestyleKeys.size());
        keys.add(poolKey);
        keys.addAll(lifestyleKeys);

        redis.execute(deleteScript, keys, encodeMemberId(command.memberId()));
    }

    /* ===== 부가 기능 (hasRoom) ===== */

    public void addUserToHasRoom(Long universityId, String gender, Long memberId) {
        redis.opsForSet().add(hasRoomKey(universityId, gender), encodeMemberId(memberId.toString()));
    }

    public void removeUserFromHasRoom(Long universityId, String gender, Long memberId){
        redis.opsForSet().remove(hasRoomKey(universityId, gender), encodeMemberId(memberId.toString()));
    }

    /* ===== 읽기/필터 (필요시 S*STORE로 서버측 교집합 가능) ===== */

    public Map<Long, Integer> filterUsersWithAttributeList(Long universityId, String gender,
        MemberStat memberStat, List<String> filterList, Boolean hasRoom) {
        Map<String, List<?>> filterMap = MemberStatExtractor.toFilterMap(memberStat, filterList);
        List<Long> filteredUserList = filterUsers(universityId, gender, filterMap, hasRoom);

        return lifestyleMatchRateCacheService
            .findMatchRates(memberStat.getMember().getId(), filteredUserList)
            .entrySet().stream()
            .sorted(Comparator.comparingInt(Map.Entry<Long, Integer>::getValue).reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    private List<Long> filterUsers(Long universityId, String gender,
        Map<String, List<?>> filters, Boolean hasRoom) {
        Set<String> basePool = getBasePool(universityId, gender);
        if (basePool == null || basePool.isEmpty()) {
            return Collections.emptyList();
        }

        List<Set<String>> groups = new ArrayList<>();
        groups.add(basePool);

        for (Map.Entry<String, List<?>> e : filters.entrySet()) {
            Set<String> u = getUnionedSetPerFilter(universityId, e.getKey(), e.getValue());
            if (u == null || u.isEmpty()) {
                return Collections.emptyList();
            }
            groups.add(u);
        }

        Set<String> result = intersectInMemory(groups);

        if (Boolean.TRUE.equals(hasRoom)) {
            return excludeUsersInHasRoom(result, universityId, gender);
        }
        return result.stream().map(Long::valueOf).toList();
    }

    private Set<String> getBasePool(Long universityId, String gender) {
        Set<String> m = redis.opsForSet().members(poolKey(universityId, gender));
        if (m == null) {
            return Collections.emptySet();
        }
        return m.stream()
            .map(this::normalizeUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());    }

    private Set<String> getUnionedSetPerFilter(Long universityId, String key, List<?> values) {
        List<String> keys = values.stream()
            .filter(Objects::nonNull)
            .map(v -> lifestyleKey(universityId, key, v.toString().trim()))
            .toList();
        if (keys.isEmpty()) {
            return null;
        }

        Set<String> acc = new HashSet<>();
        for (String k : keys) {
            Set<String> m = redis.opsForSet().members(k);
            if (m != null) {
                m.stream()
                    .map(this::normalizeUserId)
                    .filter(Objects::nonNull)
                    .forEach(acc::add);
            }
        }
        return acc;
    }


    private Set<String> intersectInMemory(List<Set<String>> sets) {
        Iterator<Set<String>> it = sets.iterator();
        Set<String> res = new HashSet<>(it.next());
        while (it.hasNext()) {
            res.retainAll(it.next());
            if (res.isEmpty()) {
                break;
            }
        }
        return res;
    }

    private List<Long> excludeUsersInHasRoom(Set<String> result, Long universityId, String gender) {
        Set<String> hasRoomSet = redis.opsForSet().members(hasRoomKey(universityId, gender));
        if (hasRoomSet == null || hasRoomSet.isEmpty()) {
            return result.stream()
                .map(this::normalizeUserId)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
        }

        Set<String> normalizedResult = result.stream()
            .map(this::normalizeUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Set<String> normalizedHasRoom = hasRoomSet.stream()
            .map(this::normalizeUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        normalizedResult.removeAll(normalizedHasRoom);

        return normalizedResult.stream()
            .map(Long::valueOf)
            .toList();
    }

    /* ===== 키/유틸 ===== */

    private String poolKey(Long universityId, String gender) {
        return String.format(POOL_PREFIX, universityId, gender);
    }

    private String hasRoomKey(Long universityId, String gender) {
        return String.format(HASROOM_KEY, universityId, gender);
    }

    private String lifestyleKey(Long universityId, String questionKey, String answerValue) {
        return String.format(LIFESTYLE_PREFIX, universityId, questionKey, answerValue);
    }

    private List<String> expandLifestyleKeys(Long universityId, Map<String, String> answers) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> e : answers.entrySet()) {
            final String q = e.getKey();
            final String raw = norm(e.getValue());
            if (raw == null || raw.isBlank()) {
                continue;
            }

            if (MULTI_VALUE_QUESTION.contains(q)) {
                for (String v : toSet(raw)) {
                    keys.add(lifestyleKey(universityId, q, v));
                }
            } else {
                keys.add(lifestyleKey(universityId, q, raw));
            }
        }
        return keys;
    }

    private Diff diffKeys(Long universityId, Map<String, String> oldAns,
        Map<String, String> newAns) {
        List<String> removes = new ArrayList<>();
        List<String> adds = new ArrayList<>();

        for (String q : newAns.keySet()) {
            String oldRaw = norm(oldAns.get(q));
            String newRaw = norm(newAns.get(q));
            if (newRaw == null || newRaw.isBlank()) {
                throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
            }
            if (Objects.equals(oldRaw, newRaw)) {
                continue;
            }

            if (MULTI_VALUE_QUESTION.contains(q)) {
                Set<String> o = toSet(oldRaw);
                Set<String> n = toSet(newRaw);
                for (String v : diff(o, n)) {
                    removes.add(lifestyleKey(universityId, q, v));
                }
                for (String v : diff(n, o)) {
                    adds.add(lifestyleKey(universityId, q, v));
                }
            } else {
                if (oldRaw != null && !oldRaw.isBlank()) {
                    removes.add(lifestyleKey(universityId, q, oldRaw));
                }
                adds.add(lifestyleKey(universityId, q, newRaw));
            }
        }
        return new Diff(removes, adds);
    }

    private Set<String> diff(Set<String> a, Set<String> b) {
        Set<String> r = new HashSet<>(a);
        r.removeAll(b);
        return r;
    }

    private Set<String> toSet(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptySet();
        }
        String[] parts = csv.split(",");
        Set<String> s = new HashSet<>();
        for (String p : parts) {
            String v = norm(p);
            if (v != null && !v.isBlank()) {
                s.add(v);
            }
        }
        return s;
    }

    private String norm(String v) {
        return v == null ? null : v.trim();
    }

    private String encodeMemberId(String memberId) {
        if (memberId == null) {
            return null;
        }
        return "\"" + memberId + "\"";
    }

    private String normalizeUserId(String raw) {
        if (raw == null) return null;
        String v = raw.trim();

        if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
            v = v.substring(1, v.length() - 1).trim();
        }

        if (!v.matches("\\d+")) {
            return null;
        }
        return v;
    }
    private record Diff(
        List<String> removes,
        List<String> adds) {

    }
}