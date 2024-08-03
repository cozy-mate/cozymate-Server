package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberStatQueryService {

    private final MemberRepository memberRepository;
    private final MemberStatRepository memberStatRepository;

    private static final Integer ADDITIONAL_SCORE = 12;
    private static final Integer ATTRIBUTE_COUNT = 19;
    private static final Integer MAX_SCORE = ADDITIONAL_SCORE * ATTRIBUTE_COUNT;

    public MemberStat getMemberStat(Long memberId) {

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        return memberStatRepository.findByMemberId(memberId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
            );
    }

    public Page<MemberStatEqualityResponseDTO> getMemberStatList(Long memberId,
        List<String> filterList, Pageable pageable) {
        //일치율의 기준이 되는 MemberStat을 가져옵니다.
        MemberStat criteriaMemberStat = memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        //필터링된 리스트들을 가져옵니다. 필터가 없을 경우, 모두 가져옵니다.
        List<MemberStat> filteredResult = memberStatRepository.getFilteredMemberStat(filterList,
            criteriaMemberStat);

        // 일치율을 계산합니다.
        List<HashMap<Member, Integer>> result = filteredResult
            .stream()
            .map(val -> convertToScore(criteriaMemberStat, val))
            .toList();
        //계산된 결과를 정렬하고, DTO로 바꿔줍니다.
        List<MemberStatEqualityResponseDTO> sortedList = result.stream()
            .flatMap(map -> map.entrySet().stream())
            .sorted(Map.Entry.<Member, Integer>comparingByValue().reversed())
            .map(entry -> new MemberStatEqualityResponseDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        //List를 Page로 변환하기 위해 아래 코드를 사용합니다.
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        //Page를 리턴합니다.
        return new PageImpl<>(sortedList.subList(start, end), pageable, sortedList.size());
    }

    private HashMap<Member, Integer> convertToScore(MemberStat criteriaMemberStat,
        MemberStat memberStat) {

        int score = 0;

        score +=
            criteriaMemberStat.getAcceptance().equals(memberStat.getAcceptance()) ? ADDITIONAL_SCORE
                : 0;
        score += criteriaMemberStat.getAdmissionYear().equals(memberStat.getAdmissionYear())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getMajor().equals(memberStat.getMajor()) ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getSmoking().equals(memberStat.getSmoking()) ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getSleepingHabit().equals(memberStat.getSleepingHabit())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getLifePattern().equals(memberStat.getLifePattern())
            ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getIntimacy().equals(memberStat.getIntimacy()) ? ADDITIONAL_SCORE
                : 0;
        score +=
            criteriaMemberStat.getCanShare().equals(memberStat.getCanShare()) ? ADDITIONAL_SCORE
                : 0;
        score +=
            criteriaMemberStat.getIsPlayGame().equals(memberStat.getIsPlayGame()) ? ADDITIONAL_SCORE
                : 0;
        score += criteriaMemberStat.getIsPhoneCall().equals(memberStat.getIsPhoneCall())
            ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getStudying().equals(memberStat.getStudying()) ? ADDITIONAL_SCORE
                : 0;
        score += criteriaMemberStat.getCleaningFrequency().equals(memberStat.getCleaningFrequency())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getPersonality().equals(memberStat.getPersonality())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getMbti().equals(memberStat.getMbti()) ? ADDITIONAL_SCORE : 0;

        score += calculateTimeScore(criteriaMemberStat.getWakeUpTime(), memberStat.getWakeUpTime());
        score += calculateTimeScore(criteriaMemberStat.getSleepingTime(),
            memberStat.getSleepingTime());
        score += calculateTimeScore(criteriaMemberStat.getTurnOffTime(),
            memberStat.getTurnOffTime());

        score += calculateSensitivityScore(criteriaMemberStat.getCleanSensitivity(),
            memberStat.getCleanSensitivity());
        score += calculateSensitivityScore(criteriaMemberStat.getNoiseSensitivity(),
            memberStat.getNoiseSensitivity());

        Integer percent = (Integer) score / MAX_SCORE;
        HashMap<Member, Integer> result = new HashMap<>();
        result.put(memberStat.getMember(), score);

        return result;
    }

    private int calculateTimeScore(Integer time1, Integer time2) {
        int timeDifference = Math.abs(time1 - time2);
        if (timeDifference == 0) {
            return ADDITIONAL_SCORE;
        } else if (timeDifference <= 1) {
            return ADDITIONAL_SCORE / 2;
        } else if (timeDifference <= 2) {
            return ADDITIONAL_SCORE / 4;
        } else {
            return 0;
        }
    }

    private int calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
        int sensitivityDifference = Math.abs(sensitivity1 - sensitivity2);
        if (sensitivityDifference == 0) {
            return ADDITIONAL_SCORE;
        } else if (sensitivityDifference == 1) {
            return ADDITIONAL_SCORE / 2;
        } else {
            return 0;
        }
    }

}
