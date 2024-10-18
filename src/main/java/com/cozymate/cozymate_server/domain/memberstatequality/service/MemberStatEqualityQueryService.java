package com.cozymate.cozymate_server.domain.memberstatequality.service;

import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberStatEqualityQueryService {

    private final MemberStatEqualityRepository memberStatEqualityRepository;

    public Map<Long, Integer> getEquality(Long memberAId, List<Long> memberIdList) {

        List<MemberStatEquality> memberStatEqualityList = memberStatEqualityRepository.findByMemberAIdAndMemberBIdIn(
            memberAId, memberIdList);

        return memberStatEqualityList.stream()
            .collect(Collectors.toMap(
                MemberStatEquality::getMemberBId,
                MemberStatEquality::getEquality
            ));
    }


}
