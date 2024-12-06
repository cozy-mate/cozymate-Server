package com.cozymate.cozymate_server.domain.room.util;

import com.cozymate.cozymate_server.domain.favorite.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomStatUtil {

    public static List<PreferenceMatchCountDTO> getPreferenceStatsMatchCounts(Member member,
        List<String> criteriaPreferenceList, List<Mate> mates, MemberStat memberStat) {

        List<PreferenceMatchCountDTO> preferenceMatchCountDTOList = criteriaPreferenceList.stream()
            .map(preference -> {
                long equalCount = mates.stream()
                    .map(mate -> mate.getMember().getMemberStat())
                    .filter(mateStat -> mateStat != null && !mateStat.getMember().getId().equals(
                        member.getId())
                        && Objects.equals(MemberStatUtil.getMemberStatField(mateStat, preference),
                        MemberStatUtil.getMemberStatField(memberStat, preference)))
                    .count();

                return PreferenceMatchCountDTO.builder()
                    .preferenceName(preference)
                    .count((int) equalCount)
                    .build();
            }).toList();

        return preferenceMatchCountDTOList;
    }

    public static List<PreferenceMatchCountDTO> getPreferenceStatsMatchCountsWithoutMemberStat(
        List<String> criteriaPreferenceList) {
        return criteriaPreferenceList.stream()
            .map(preference -> {
                return PreferenceMatchCountDTO.builder()
                    .preferenceName(preference)
                    .count(null)
                    .build();
            }).toList();
    }

    public static Integer getCalculateRoomEquality(Map<Long, Integer> equalityMap){
        List<Integer> roomEquality = equalityMap.values().stream()
            .toList();
        int sum = roomEquality.stream().mapToInt(Integer::intValue).sum();
        return roomEquality.isEmpty() ? null : sum / roomEquality.size();

    }
}