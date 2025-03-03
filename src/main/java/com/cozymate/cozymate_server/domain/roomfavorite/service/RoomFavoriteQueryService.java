package com.cozymate.cozymate_server.domain.roomfavorite.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.converter.RoomFavoriteConverter;
import com.cozymate.cozymate_server.domain.roomfavorite.dto.response.RoomFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagQueryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomFavoriteQueryService {

    private final RoomFavoriteRepository roomFavoriteRepository;
    private final MateRepository mateRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomHashtagQueryService roomHashtagQueryService;


    public List<RoomFavoriteResponseDTO> getFavoriteRoomList(Member member) {
        List<RoomFavorite> roomFavoriteList = roomFavoriteRepository.findByMember(member);

        if (roomFavoriteList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Room, Long> roomFavoriteIdMap = roomFavoriteList.stream()
            .collect(Collectors.toMap(RoomFavorite::getRoom, RoomFavorite::getId));

        List<Room> findFavoriteRoomIdList = new ArrayList<>(roomFavoriteIdMap.keySet());

        // 방 상태가 disable과 그 외로 분리
        Map<Boolean, List<Room>> partitionedRoomStatusMap = findFavoriteRoomIdList.stream()
            .collect(
                Collectors.partitioningBy(room -> !room.getStatus().equals(RoomStatus.DISABLE)));

        // 방 상태가 disable이 아닌 방 리스트
        List<Room> nonDisableFavoriteRoomList = partitionedRoomStatusMap.get(true);

        // 방 상태가 disable이 아닌 방 리스트에서 방 인원이 꽉찬 방과 아닌 방으로 분리
        Map<Boolean, List<Room>> partitionedMateNumMap = nonDisableFavoriteRoomList.stream()
            .collect(
                Collectors.partitioningBy(room -> room.getNumOfArrival() != room.getMaxMateNum()));

        // 방이 꽉 차지 않은 방 리스트
        List<Room> responseRoomList = partitionedMateNumMap.get(true);

        // <방 id, 해당 방의 mate 리스트>
        Map<Long, List<Mate>> roomIdMatesMap = responseRoomList.stream().collect(
            Collectors.toMap(Room::getId,
                room -> mateRepository.findFetchMemberAndMemberStatByRoom(room,
                    EntryStatus.JOINED)));

        // 로그인 사용자의 선호 스탯 4가지를 리스트로 가져온다
        List<String> criteriaPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        // 로그인 사용자의 member stat
        MemberStat memberStat = member.getMemberStat();

        List<RoomFavoriteResponseDTO> favoriteRoomResponseList = responseRoomList.stream()
            .map(room -> {
                List<Mate> mates = roomIdMatesMap.get(room.getId());

                // 선호 스탯 일치 횟수 계산
                List<PreferenceMatchCountDTO> preferenceStatsMatchCountList =
                    Objects.nonNull(memberStat)
                        ? RoomStatUtil.getPreferenceStatsMatchCounts(member, criteriaPreferenceList,
                        mates, memberStat)
                        : RoomStatUtil.getPreferenceStatsMatchCountsWithoutMemberStat(
                            criteriaPreferenceList);

                // 로그인 사용자와 mate들의 멤버 스탯 "일치율" 계산
                Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                    member.getId(), mates.stream()
                        .map(mate -> mate.getMember().getId())
                        .toList()
                );

                Map<Long, List<String>> roomHashtagsMap = roomHashtagQueryService.getRoomHashtagsByRooms(responseRoomList);

                // 로그인 사용자와 방 일치율 계산
                Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);

                // 방 해시태그 조회
                List<String> roomHashTags = roomHashtagsMap.getOrDefault(room.getId(), List.of());

                return RoomFavoriteConverter.toRoomFavoriteResponseDTO(
                    roomFavoriteIdMap.get(room), room, roomEquality,
                    preferenceStatsMatchCountList, roomHashTags, mates.size()
                );
            })
            .toList();

        // 조회 조건에 맞지 않는 방 찜 삭제 처리
        deleteFavoriteRoom(partitionedRoomStatusMap.get(false),
            partitionedMateNumMap.get(false));

        return favoriteRoomResponseList;
    }

    private void deleteFavoriteRoom(List<Room> disableFavoriteRoomList,
        List<Room> fullRoomList) {

        List<Long> deleteTargetRoomIdList = new ArrayList<>();

        // 방 status가 disable인 방 리스트, 존재한다면 방이 있다면 deleteTargetRoomIdList에 추가
        if (!disableFavoriteRoomList.isEmpty()) {
            deleteTargetRoomIdList.addAll(disableFavoriteRoomList.stream()
                .map(Room::getId)
                .toList());
        }

        // 인원이 가득 찬 방 리스트, 존재한다면 deleteTargetRoomIdList에 추가
        if (!fullRoomList.isEmpty()) {
            deleteTargetRoomIdList.addAll(fullRoomList.stream()
                .map(Room::getId)
                .toList());
        }

        // deletedRoomIdList에 들어 있는 roomId에 해당하는 방 찜 삭제 처리
        if (!deleteTargetRoomIdList.isEmpty()) {
            roomFavoriteRepository.deleteAllByRoomIds(deleteTargetRoomIdList);
        }
    }
}
