package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberCachingDTO;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepositoryService memberRepositoryService;
    private final ObjectMapper objectMapper;

    private static final String MEMBER_CACHE_KEY_PREFIX = "MEMBER:memberId:";

    public Map<Long, MemberCachingDTO> getCachingMemberMap(List<Long> memberIdList) {
        Map<Long, MemberCachingDTO> cachingMemberMap = new HashMap<>();
        List<Long> cacheMissIdList = new ArrayList<>();

        memberIdList.forEach(
            id -> {
                MemberCachingDTO memberCachingDTO = findMemberCachingDTO(id);

                if (Objects.isNull(memberCachingDTO)) {
                    cacheMissIdList.add(id);
                } else {
                    cachingMemberMap.put(id, memberCachingDTO);
                }
            }
        );

        // 캐시 miss 처리
        if (!cacheMissIdList.isEmpty()) {
            List<Member> findMissMemberList = memberRepositoryService.getMemberListByIds(
                cacheMissIdList);
            findMissMemberList.forEach(
                m -> {
                    MemberCachingDTO memberCachingDTO = MemberConverter.toMemberCachingDTO(
                        m.getNickname(), m.getPersona());
                    cachingMemberMap.put(m.getId(), memberCachingDTO);
                    saveMemberCachingDTO(m.getId(), memberCachingDTO);
                }
            );
        }

        return cachingMemberMap;
    }

    public void saveMemberCachingDTO(Long memberId, MemberCachingDTO memberCachingDTO) {
        redisTemplate.opsForValue()
            .set(MEMBER_CACHE_KEY_PREFIX + memberId, memberCachingDTO, 1, TimeUnit.HOURS);
    }

    public MemberCachingDTO findMemberCachingDTO(Long memberId) {
        return objectMapper.convertValue(
            redisTemplate.opsForValue().get(MEMBER_CACHE_KEY_PREFIX + memberId),
            MemberCachingDTO.class);
    }

    public void deleteMemberCachingDTO(Long memberId) {
        redisTemplate.delete(MEMBER_CACHE_KEY_PREFIX + memberId);
    }
}
