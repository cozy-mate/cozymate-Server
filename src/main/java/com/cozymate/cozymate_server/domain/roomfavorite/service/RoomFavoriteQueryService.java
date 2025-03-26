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
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.converter.RoomFavoriteConverter;
import com.cozymate.cozymate_server.domain.roomfavorite.dto.response.RoomFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepositoryService;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomFavoriteQueryService {

    private final MateRepository mateRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomHashtagQueryService roomHashtagQueryService;
    private final RoomFavoriteRepositoryService roomFavoriteRepositoryService;

    public PageResponseDto<List<RoomFavoriteResponseDTO>> getFavoriteRoomList(Member member,
        int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<RoomFavorite> roomFavoriteList = roomFavoriteRepositoryService.getRoomFavoriteListByMember(
            member, pageRequest);

        if (roomFavoriteList.isEmpty()) {
            return PageResponseDto.<List<RoomFavoriteResponseDTO>>builder()
                .page(page)
                .hasNext(false)
                .result(List.of())
                .build();
        }

        Map<Room, Long> roomFavoriteIdMap = roomFavoriteList.stream()
            .collect(Collectors.toMap(RoomFavorite::getRoom, RoomFavorite::getId));

        List<Room> findFavoriteRoomList = new ArrayList<>(roomFavoriteIdMap.keySet());

        // <방 id, 해당 방의 mate 리스트>
        Map<Long, List<Mate>> roomIdMatesMap = findFavoriteRoomList.stream().collect(
            Collectors.toMap(Room::getId,
                room -> mateRepository.findFetchMemberAndMemberStatByRoom(room,
                    EntryStatus.JOINED)));

        // 로그인 사용자의 선호 스탯 4가지를 리스트로 가져온다
        List<String> criteriaPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        // 로그인 사용자의 member stat
        MemberStat memberStat = member.getMemberStat();

        Map<Long, List<String>> roomHashtagsMap = roomHashtagQueryService.getRoomHashtagsByRooms(
            findFavoriteRoomList);

        List<RoomFavoriteResponseDTO> favoriteRoomResponseList = findFavoriteRoomList.stream()
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

        return PageResponseDto.<List<RoomFavoriteResponseDTO>>builder()
            .page(page)
            .hasNext(roomFavoriteList.hasNext())
            .result(favoriteRoomResponseList)
            .build();
    }
}
