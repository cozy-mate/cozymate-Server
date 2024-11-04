package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import com.cozymate.cozymate_server.domain.memberstatpreference.repository.MemberStatPreferenceRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponseList;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Tuple;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomRecommendService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberStatRepository memberStatRepository;
    private final MemberStatEqualityRepository memberStatEqualityRepository;
    private final MemberStatPreferenceRepository memberStatPreferenceRepository;

    // TODO: 대규모 리팩토링 필요
    public RoomRecommendationResponseList getRecommendationList(Member member, int size) {

        List<Room> roomList = roomRepository.findAllByRoomType(RoomType.PUBLIC);
        Map<Long, List<Mate>> roomMateMap = groupMatesByRoom(mateRepository.findAll());
        Map<Long, Integer> roomEqualityMap = calculateRoomEqualityMap(roomList, member,
            roomMateMap);

        List<Pair<Long, Integer>> sortedRoomList = roomEqualityMap.entrySet().stream()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
            .sorted((pair1, pair2) -> pair2.getRight().compareTo(pair1.getRight())) // 직접 람다식으로 비교
            .limit(size)
            .toList();

        MemberStatPreference memberStatPreference = memberStatPreferenceRepository.findByMemberId(
                member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS));
        List<String> preferenceList = Arrays.asList(
            memberStatPreference.getSelectedPreferences().split(","));

        return RoomRecommendationResponseList.builder()
            .recommendations(
                buildRoomRecommendationResponses(member, sortedRoomList, roomMateMap, roomList,
                    preferenceList))
            .build();
    }

    private Map<Long, List<Mate>> groupMatesByRoom(List<Mate> mateList) {
        return mateList.stream().collect(Collectors.groupingBy(mate -> mate.getRoom().getId()));
    }

    private Map<Long, Integer> calculateRoomEqualityMap(List<Room> roomList, Member member,
        Map<Long, List<Mate>> roomMateMap) {
        Map<Long, Integer> roomEqualityMap = new HashMap<>();
        for (Room room : roomList) {
            List<Member> memberList = roomMateMap.getOrDefault(room.getId(),
                    Collections.emptyList()).stream()
                .map(Mate::getMember)
                .toList();
            List<Integer> equalityList = memberStatEqualityRepository.findByMemberAIdAndMemberBIdIn(
                    member.getId(), memberList.stream().map(Member::getId).toList()).stream()
                .map(MemberStatEquality::getEquality)
                .toList();
            int averageEquality = equalityList.isEmpty() ? 0
                : equalityList.stream().reduce(Integer::sum).orElse(0) / equalityList.size();
            roomEqualityMap.put(room.getId(), averageEquality);
        }
        return roomEqualityMap;
    }

    private List<RoomRecommendationResponse> buildRoomRecommendationResponses(
        Member member, List<Pair<Long, Integer>> sortedRoomList, Map<Long, List<Mate>> roomMateMap,
        List<Room> roomList, List<String> preferenceList) {

        Map<Long, Room> roomMap = roomList.stream()
            .collect(Collectors.toMap(Room::getId, room -> room));

        return sortedRoomList.stream()
            .map(pair -> createRoomRecommendationResponse(member, pair, roomMap.get(pair.getLeft()),
                roomMateMap, preferenceList))
            .toList();
    }

    private RoomRecommendationResponse createRoomRecommendationResponse(
        Member member, Pair<Long, Integer> pair, Room room, Map<Long, List<Mate>> roomMateMap,
        List<String> preferenceList) {

        Map<String, Integer> preferenceMap = new HashMap<>();
        List<Long> mateMemberIds = roomMateMap.getOrDefault(pair.getLeft(), Collections.emptyList())
            .stream()
            .map(mate -> mate.getMember().getId())
            .toList();

        Tuple memberStat = memberStatRepository.findMemberStatAndMemberIdByMemberId(
            member.getId());
        List<Tuple> memberStatList = memberStatRepository.findMemberStatsAndMemberIdsByMemberIdsWithAlias(
            new HashSet<>(mateMemberIds));

        preferenceList.forEach(preference ->
            memberStatList.forEach(tuple -> {
                if (tuple.get(preference) == memberStat.get(preference)) {
                    preferenceMap.merge(preference, 1, Integer::sum);
                }
            })
        );

        return RoomRecommendationResponse.builder()
            .roomId(pair.getLeft())
            .name(room.getName())
            .hashtags(room.getRoomHashtags().stream()
                .map(roomHashtag -> roomHashtag.getHashtag().getHashtag())
                .toList())
            .equality(pair.getRight())
            .maxMateNum(room.getMaxMateNum())
            .numOfArrival(room.getNumOfArrival())
            .equalMemberStatNum(preferenceMap)
            .build();
    }
}
