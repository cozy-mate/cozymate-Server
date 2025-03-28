package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository.LifestyleMatchRateRepositoryService;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.util.MemberMatchRateCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LifestyleMatchRateService {

    private final LifestyleMatchRateRepositoryService lifestyleMatchRateRepositoryService;
    private final MemberStatRepository memberStatRepository;
    private final UniversityRepository universityRepository;

    private static final Integer NO_EQUALITY = null;

    public Map<Long, Integer> getMatchRateWithMemberIdAndIdList(Long memberId,
        List<Long> memberIdList) {
        if (memberIdList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<LifestyleMatchRate.LifestyleMatchRateId> idList = memberIdList.stream()
            .map(id -> new LifestyleMatchRate.LifestyleMatchRateId(memberId, id))
            .toList();

        return createMatchRateMap(memberId,
            lifestyleMatchRateRepositoryService.getLifestyleMatchRateListByIdList(idList));
    }

    public Map<Long, Integer> getMatchRate(Long memberId) {
        return createMatchRateMap(memberId,
            lifestyleMatchRateRepositoryService.getLifestyleMatchRateListBySingleMemberId(memberId));
    }

    public Integer getSingleMatchRate(Long memberA, Long memberB) {
        return Optional.ofNullable(
                lifestyleMatchRateRepositoryService.getLifestyleMatchRateByIdOrNoMatchRate(
                    new LifestyleMatchRate.LifestyleMatchRateId(memberA, memberB)))
            .map(LifestyleMatchRate::getMatchRate)
            .orElse(NO_EQUALITY);
    }

    @Transactional
    public void saveLifeStyleMatchRate(MemberStat memberStat) {
        List<MemberStat> targetMemberStats = getTargetMemberStats(memberStat);

        List<LifestyleMatchRate> lifestyleMatchRateList = targetMemberStats.stream()
            .map(targetStat -> createMatchRate(memberStat, targetStat))
            .toList();

        lifestyleMatchRateRepositoryService.createAndUpdateLifestyleMatchRateList(lifestyleMatchRateList);
    }

    @Transactional
    public void calculateAllLifeStyleMatchRate() {
        universityRepository.findAll()
            .forEach(university -> Arrays.stream(Gender.values())
                .forEach(gender -> calculateAllLifeStyleMatchRateWithSameUniversityAndGender(university, gender)));
    }

    private void calculateAllLifeStyleMatchRateWithSameUniversityAndGender(University university, Gender gender) {
        List<MemberStat> memberStatList = memberStatRepository.findByMemberUniversityAndGender(gender, university.getId());

        List<LifestyleMatchRate> lifestyleMatchRateList = new ArrayList<>();

        for (int i = 0; i < memberStatList.size(); i++) {
            for (int j = i + 1; j < memberStatList.size(); j++) {
                lifestyleMatchRateList.add(createMatchRate(memberStatList.get(i), memberStatList.get(j)));
            }
        }

        lifestyleMatchRateRepositoryService.createAndUpdateLifestyleMatchRateList(lifestyleMatchRateList);
    }

    private List<MemberStat> getTargetMemberStats(MemberStat memberStat) {
        return memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
            memberStat.getMember().getGender(),
            memberStat.getMember().getUniversity().getId(),
            memberStat.getMember().getId());
    }

    private LifestyleMatchRate createMatchRate(MemberStat memberA, MemberStat memberB) {
        int matchRate = MemberMatchRateCalculator.calculateLifestyleMatchRate(
            memberA.getLifestyle(), memberB.getLifestyle());

        return new LifestyleMatchRate(memberA.getMember().getId(), memberB.getMember().getId(), matchRate);
    }

    private Map<Long, Integer> createMatchRateMap(Long memberId, List<LifestyleMatchRate> lifestyleMatchRateList) {
        return lifestyleMatchRateList.stream()
            .collect(Collectors.toMap(
                rate -> rate.getId().getMemberA().equals(memberId) ? rate.getId().getMemberB() : rate.getId().getMemberA(),
                LifestyleMatchRate::getMatchRate
            ));
    }
}