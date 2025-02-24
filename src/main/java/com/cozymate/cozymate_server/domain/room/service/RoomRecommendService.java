package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomRecommendConverter;
import com.cozymate.cozymate_server.domain.room.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomRecommendationResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomSortType;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomRecommendService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberStatRepository memberStatRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomHashtagRepository roomHashtagRepository;

    public PageResponseDto<List<RoomRecommendationResponseDTO>> getRecommendationList(Member member,
        int size, int page, RoomSortType sortType) {

        // 사용자의 preference 데이터는 무조건 있어야함
        List<String> memberPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        List<Room> roomList = getRoomList(member);

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
        Map<Long, List<Mate>> roomMateMap = mateRepository.findAllFetchMemberAndMemberStatByEntryStatus(
                EntryStatus.JOINED)
            .stream().collect(Collectors.groupingBy(mate -> mate.getRoom().getId()));

        Map<Long, Integer> roomEqualityMap = calculateRoomEqualityMap(roomList, member,
            roomMateMap);

        // 방 해시태그 조회
        Map<Long, List<String>> roomHashtagsMap = roomHashtagRepository.findByRoomIds(
                roomList.stream().map(Room::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(
                roomHashtag -> roomHashtag.getRoom().getId(),
                Collectors.mapping(roomHashtag -> roomHashtag.getHashtag().getHashtag(), Collectors.toList())
            ));

        // null을 가장 후순위로 처리
        List<Pair<Long, Integer>> sortedRoomList = getSortedRoomListBySortType(roomEqualityMap,
            roomMap, sortType, page, size);
        boolean hasNext = sortedRoomList.size() > size;
        sortedRoomList = sortedRoomList.stream().limit(size).toList();

        List<RoomRecommendationResponseDTO> roomRecommendationResponseDTOList = buildRoomRecommendationResponseList(
            member, sortedRoomList, roomMateMap, roomList, memberPreferenceList, roomHashtagsMap);

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
            List<Mate> mates = roomMateMap.get(room.getId());

            Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                member.getId(),
                mates.stream()
                    .map(mate -> mate.getMember().getId())
                    .toList()
            );

            Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);

            roomEqualityMap.put(room.getId(), roomEquality);
        });

        return roomEqualityMap;
    }

    private List<Room> getRoomList(Member member) {
        // 모든 공개 방을 가져옴 (Public 방 중에서, Disable 상태가 아니며, 인원이 꽉 차지 않은 방)
        List<Room> roomList = roomRepository.findAllRoomListCanDisplay(RoomType.PUBLIC, RoomStatus.DISABLE);

        // 대학교 필터링, 성별 필터링 -> TODO: 추후 room으로 해당 데이터가 저장되면 삭제 후 위 쿼리 수정
        List<Mate> managerList = mateRepository.findAllByRoomIdListAndIsRoomManagerAndEntryStatus(
            roomList.stream().map(Room::getId).toList(), Boolean.TRUE, EntryStatus.JOINED);

        // 대학 필터링
        managerList.stream()
            .filter(manager -> !manager.getMember().getUniversity().getId().equals(member.getUniversity().getId()))
            .forEach(manager -> roomList.remove(manager.getRoom()));

        // 성별 필터링
        managerList.stream()
            .filter(manager -> !manager.getMember().getGender().equals(member.getGender()))
            .forEach(manager -> roomList.remove(manager.getRoom()));

        // 본인이 참여한 방 가져오기
        Optional<Room> joinedRoom = mateRepository.findByMemberAndEntryStatus(member, EntryStatus.JOINED)
            .map(Mate::getRoom);

        // 본인이 이미 참여한 방을 제외
        return roomList.stream()
            .filter(room -> joinedRoom.map(joined -> !joined.equals(room)).orElse(true))
            .toList();
    }


    private List<RoomRecommendationResponseDTO> buildRoomRecommendationResponseList(
        Member member, List<Pair<Long, Integer>> sortedRoomList, Map<Long, List<Mate>> roomMateMap,
        List<Room> roomList, List<String> preferenceList, Map<Long, List<String>> roomHashtagsMap) {

        Map<Long, Room> roomMap = roomList.stream()
            .collect(Collectors.toMap(Room::getId, room -> room));

        return sortedRoomList.stream()
            .map(pair -> createRoomRecommendationResponse(member, pair, roomMap.get(pair.getLeft()),
                roomMateMap, preferenceList, roomHashtagsMap))
            .toList();
    }

    private RoomRecommendationResponseDTO createRoomRecommendationResponse(
        Member member, Pair<Long, Integer> pair, Room room, Map<Long, List<Mate>> roomMateMap,
        List<String> preferenceList, Map<Long, List<String>> roomHashtagsMap) {

        List<PreferenceMatchCountDTO> preferenceStatsMatchCounts = RoomStatUtil.getPreferenceStatsMatchCounts(
            member, preferenceList, roomMateMap.get(room.getId()), member.getMemberStat());

        // roomHashtagsMap에서 해시태그 가져오기
        List<String> hashtags = roomHashtagsMap.getOrDefault(room.getId(), List.of());

        return RoomRecommendConverter.toRoomRecommendationResponse(room, pair,
            preferenceStatsMatchCounts, hashtags);
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
        List<PreferenceMatchCountDTO> preferenceMatchCountDTOList = RoomStatUtil.getPreferenceStatsMatchCountsWithoutMemberStat(
            memberPreferenceList);

        // 방 해시태그 조회
        Map<Long, List<String>> roomHashtagsMap = roomHashtagRepository.findByRoomIds(
                roomList.stream().map(Room::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(
                roomHashtag -> roomHashtag.getRoom().getId(),
                Collectors.mapping(roomHashtag -> roomHashtag.getHashtag().getHashtag(), Collectors.toList())
            ));

        List<RoomRecommendationResponseDTO> responseList =
            roomList.stream()
                .map(
                    room -> RoomRecommendConverter.toRoomRecommendationResponseWhenNoMemberStat(
                        room, preferenceMatchCountDTOList, roomHashtagsMap.getOrDefault(room.getId(), List.of())
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
