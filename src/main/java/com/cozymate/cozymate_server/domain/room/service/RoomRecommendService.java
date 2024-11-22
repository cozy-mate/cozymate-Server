package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomRecommendConverter;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomRecommendationResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomSortType;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomRecommendService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberStatRepository memberStatRepository;
    private final MemberStatEqualityRepository memberStatEqualityRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;

    public PageResponseDto<List<RoomRecommendationResponseDTO>> getRecommendationList(Member member,
        int size, int page, RoomSortType sortType) {

        // 사용자의 preference 데이터는 무조건 있어야함
        List<String> memberPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        // TODO: 대학, 성별, 시기 필터링
        // 모든 방을 가져옴 (Public 방 중에서, Disble 상태가 아니며, 인원이 꽉 차지 않은 방)
        List<Room> roomList = roomRepository.findAllRoomListCanDisplay(RoomType.PUBLIC,
            RoomStatus.DISABLE);

        // MemberStat을 가져오되, 없으면 무작위로 방 추천을 진행
        Optional<MemberStat> memberStat = memberStatRepository.findByMemberId(member.getId());
        if (memberStat.isEmpty()) {
            return getRoomRecommendationResponseListWhenNoMemberStat(size, page,
                memberPreferenceList, roomList);
        }

        // roomId와 room으로 구성됨
        Map<Long, Room> roomMap = roomList.stream()
            .collect(Collectors.toMap(Room::getId, room -> room));

        // roomId에 해당하는 MateList로 구성됨
        Map<Long, List<Mate>> roomMateMap = mateRepository.findAllByEntryStatus(EntryStatus.JOINED)
            .stream().collect(Collectors.groupingBy(mate -> mate.getRoom().getId()));

        // roomId와 member의 일치율로 구성됨
        Map<Long, Integer> roomEqualityMap = calculateRoomEqualityMap(roomList, member,
            roomMateMap);

        // null을 가장 후순위로 처리
        List<Pair<Long, Integer>> sortedRoomList = getSortedRoomListBySortType(roomEqualityMap,
            roomMap, sortType, page, size);
        boolean hasNext = sortedRoomList.size() > size;
        sortedRoomList = sortedRoomList.stream().limit(size).toList();

        List<RoomRecommendationResponseDTO> roomRecommendationResponseDTOList = buildRoomRecommendationResponseList(
            member, sortedRoomList, roomMateMap, roomList, memberPreferenceList);

        return PageResponseDto.<List<RoomRecommendationResponseDTO>>builder()
            .page(page)
            .hasNext(hasNext)
            .result(roomRecommendationResponseDTOList)
            .build();

    }

    private Map<Long, Integer> calculateRoomEqualityMap(List<Room> roomList, Member member,
        Map<Long, List<Mate>> roomMateMap) {
        Map<Long, Integer> roomEqualityMap = new HashMap<>();

        roomList.forEach(room -> {
            List<Member> memberList = roomMateMap.get(room.getId()).stream()
                .map(Mate::getMember)
                .toList();
            List<Integer> equalityList = memberStatEqualityRepository.findByMemberAIdAndMemberBIdIn(
                    member.getId(), memberList.stream().map(Member::getId).toList()).stream()
                .map(MemberStatEquality::getEquality)
                .toList();
            // TODO: null 처리 필요
            Integer averageEquality = equalityList.isEmpty() ? null
                : equalityList.stream().reduce(Integer::sum).orElse(null) / equalityList.size();
            roomEqualityMap.put(room.getId(), averageEquality);
        });

        return roomEqualityMap;
    }

    private List<RoomRecommendationResponseDTO> buildRoomRecommendationResponseList(
        Member member, List<Pair<Long, Integer>> sortedRoomList, Map<Long, List<Mate>> roomMateMap,
        List<Room> roomList, List<String> preferenceList) {

        Map<Long, Room> roomMap = roomList.stream()
            .collect(Collectors.toMap(Room::getId, room -> room));

        return sortedRoomList.stream()
            .map(pair -> createRoomRecommendationResponse(member, pair, roomMap.get(pair.getLeft()),
                roomMateMap, preferenceList))
            .toList();
    }

    private RoomRecommendationResponseDTO createRoomRecommendationResponse(
        Member member, Pair<Long, Integer> pair, Room room, Map<Long, List<Mate>> roomMateMap,
        List<String> preferenceList) {

        Map<String, Integer> preferenceMap = new HashMap<>();
        List<Long> mateMemberIds = roomMateMap.getOrDefault(pair.getLeft(), Collections.emptyList())
            .stream()
            .map(mate -> mate.getMember().getId())
            .toList();

        MemberStat myMemberStat = memberStatRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
        Map<String, Object> myPreference = MemberStatUtil.getMemberStatFields(myMemberStat,
            preferenceList);
        List<MemberStat> memberStatList = memberStatRepository.findAllById(mateMemberIds);

        preferenceList.forEach(preference -> {
            preferenceMap.put(preference, 0);
            memberStatList.forEach(memberStat -> {
                Map<String, Object> memberPreference = MemberStatUtil.getMemberStatFields(
                    memberStat,
                    preferenceList);
                if (myPreference.get(preference).equals(memberPreference.get(preference))) {
                    preferenceMap.merge(preference, 1, Integer::sum);
                }
            });
        });

        return RoomRecommendConverter.toRoomRecommendationResponse(room, pair, preferenceMap);
    }

    private List<Pair<Long, Integer>> getSortedRoomListBySortType(
        Map<Long, Integer> roomEqualityMap, Map<Long, Room> roomMap, RoomSortType sortType,
        int page, int size) {
        int sizeForCheckNextPage = size + 1;
        return switch (sortType) {
            case LATEST -> // 최신순으로 정렬한 후, 동일한 일자면 일치율로 정렬
                roomEqualityMap.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(
                        pair -> roomMap.get(pair.getLeft()).getCreatedAt(),
                        Comparator.nullsLast(Comparator.reverseOrder()))) // 직접 람다식으로 비교
                    .skip((long) page * size)
                    .limit(sizeForCheckNextPage)
                    .toList();
            case AVERAGE_RATE -> // 일치율순으로 정렬
                roomEqualityMap.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(Pair::getRight,
                        Comparator.nullsLast(Comparator.reverseOrder()))) // 직접 람다식으로 비교
                    .skip((long) page * size)
                    .limit(sizeForCheckNextPage)
                    .toList();
            case CLOSING_SOON -> // 인원이 적게 남은 순으로 정렬한 후, 동일한 값이면 일치율로 정렬
                roomEqualityMap.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                    .filter(pair -> pair.getRight() != null && roomMap.containsKey(
                        pair.getLeft())) // 유효한 pair만 포함
                    .sorted(Comparator.comparing(Pair::getRight,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                    .sorted(Comparator.comparing(
                        pair -> roomMap.get(pair.getLeft()).getMaxMateNum() - roomMap.get(
                            pair.getLeft()).getNumOfArrival(),
                        Comparator.naturalOrder())) // 추가 정렬 기준
                    .skip((long) page * size)
                    .limit(sizeForCheckNextPage)
                    .toList();
        };
    }

    private PageResponseDto<List<RoomRecommendationResponseDTO>> getRoomRecommendationResponseListWhenNoMemberStat(
        int size, int page, List<String> memberPreferenceList, List<Room> roomList) {
        Map<String, Integer> preferenceMap = new HashMap<>();
        memberPreferenceList.forEach(preference -> preferenceMap.put(preference, null));

        List<RoomRecommendationResponseDTO> responseList =
            roomList.stream()
                .map(
                    room -> RoomRecommendConverter.toRoomRecommendationResponseWhenNoMemberStat(
                        room, preferenceMap
                    ))
                .skip((long) page * size)
                .limit(size)
                .toList();
        return PageResponseDto.<List<RoomRecommendationResponseDTO>>builder()
            .page(page)
            .hasNext(roomList.size() > (page + 1) * size)
            .result(responseList)
            .build();
    }
}
