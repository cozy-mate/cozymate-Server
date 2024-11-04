package com.cozymate.cozymate_server.domain.favorite.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.converter.FavoriteConverter;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteMemberResponse;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteRoomResponse;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;
    private final MemberStatEqualityQueryService memberStatEqualityQueryService;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final RoomHashtagRepository roomHashtagRepository;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;

    public List<FavoriteMemberResponse> getFavoriteMemberList(Member member) {
        List<Favorite> favoriteList = favoriteRepository.findByMemberAndFavoriteType(
            member, FavoriteType.MEMBER);

        if (favoriteList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> memberIdFavoriteIdMap = favoriteList.stream()
            .collect(Collectors.toMap(Favorite::getTargetId, Favorite::getId));

        List<Long> favoriteMemberIdList = new ArrayList<>(memberIdFavoriteIdMap.keySet());

        List<Member> favoriteMemberList = memberRepository.findAllById(favoriteMemberIdList);

        Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(member.getId(),
            favoriteMemberIdList);

        List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        List<FavoriteMemberResponse> favoriteMemberResponseList = favoriteMemberList.stream()
            .map(favoriteMember -> {
                Map<String, Object> preferences = MemberStatUtil.getMemberStatFields(
                    favoriteMember.getMemberStat(), criteriaPreferences);

                return FavoriteConverter.toFavoriteMemberResponse(
                    memberIdFavoriteIdMap.get(favoriteMember.getId()),
                    equalityMap.get(favoriteMember.getId()),
                    MemberStatConverter.toPreferenceResponseDTO(favoriteMember.getMemberStat(),
                        preferences));
            }).toList();

        return favoriteMemberResponseList;
    }

    public List<FavoriteRoomResponse> getFavoriteRoomList(Member member) {
        List<Favorite> favoriteList = favoriteRepository.findByMemberAndFavoriteType(member,
            FavoriteType.ROOM);

        if (favoriteList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> roomIdFavoriteIdMap = favoriteList.stream()
            .collect(Collectors.toMap(Favorite::getTargetId, Favorite::getId));

        List<Long> favoriteRoomIdList = new ArrayList<>(roomIdFavoriteIdMap.keySet());

        List<Room> favoriteRoomList = roomRepository.findAllById(favoriteRoomIdList);

        Map<Long, List<Mate>> roomIdMatesMap = favoriteRoomList.stream().collect(
            Collectors.toMap(Room::getId, room -> mateRepository.findFetchMemberByRoom(room)));

        MemberStat memberStat = member.getMemberStat();
        List<FavoriteRoomResponse> favoriteRoomResponseList = favoriteRoomList.stream()
            .map(room -> {
                List<Mate> mates = roomIdMatesMap.get(room.getId());

                long wakeUptimeEqualNum = mates.stream()
                    .map(Mate::getMember)
                    .map(Member::getMemberStat)
                    .filter(mateStat -> mateStat != null && mateStat.getWakeUpTime()
                        .equals(memberStat.getWakeUpTime()))
                    .count();

                long sleepingTimeEqualNum = mates.stream()
                    .map(Mate::getMember)
                    .map(Member::getMemberStat)
                    .filter(mateStat -> mateStat != null && mateStat.getSleepingTime()
                        .equals(memberStat.getSleepingTime()))
                    .count();

                long noiseSensitivityEqualNum = mates.stream()
                    .map(Mate::getMember)
                    .map(Member::getMemberStat)
                    .filter(mateStat -> mateStat != null && mateStat.getNoiseSensitivity()
                        .equals(memberStat.getNoiseSensitivity()))
                    .count();

                long cleanSensitivityEqualNum = mates.stream()
                    .map(Mate::getMember)
                    .map(Member::getMemberStat)
                    .filter(mateStat -> mateStat != null && mateStat.getCleanSensitivity()
                        .equals(memberStat.getCleanSensitivity()))
                    .count();

                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(
                    member.getId(), mates.stream().map(mate -> mate.getMember().getId())
                        .collect(Collectors.toList()));

                Integer roomEquality = getCalculateRoomEquality(member.getId(), equalityMap);

                List<String> roomHashTags = roomHashtagRepository.findHashtagsByRoomId(
                    room.getId());

                return FavoriteConverter.toFavoriteRoomResponse(
                    roomIdFavoriteIdMap.get(room.getId()), room, roomEquality,
                    (int) wakeUptimeEqualNum, (int) sleepingTimeEqualNum,
                    (int) noiseSensitivityEqualNum, (int) cleanSensitivityEqualNum,
                    roomHashTags, mates.size()
                );
            })
            .toList();

        return favoriteRoomResponseList;
    }

    private Integer getCalculateRoomEquality(Long memberId, Map<Long, Integer> equalityMap) {
        List<Integer> roomEquality = equalityMap.entrySet().stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (roomEquality.isEmpty()) {
            return 0;
        }

        return (int) Math.round(roomEquality.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0));
    }
}