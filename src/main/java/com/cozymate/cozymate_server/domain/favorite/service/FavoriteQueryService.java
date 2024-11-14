package com.cozymate.cozymate_server.domain.favorite.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.converter.FavoriteConverter;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteMemberResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteRoomResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.PreferenceMatchCountDTO;
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
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    public List<FavoriteMemberResponseDTO> getFavoriteMemberList(Member member) {
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

        List<FavoriteMemberResponseDTO> favoriteMemberResponseDTOList = favoriteMemberList.stream()
            .map(favoriteMember -> {
                Map<String, Object> preferences = MemberStatUtil.getMemberStatFields(
                    favoriteMember.getMemberStat(), criteriaPreferences);

                return FavoriteConverter.toFavoriteMemberResponseDTO(
                    memberIdFavoriteIdMap.get(favoriteMember.getId()),
                    MemberStatConverter.toPreferenceResponseDTO(favoriteMember.getMemberStat(),
                        preferences, equalityMap.get(favoriteMember.getId())));
            }).toList();

        return favoriteMemberResponseDTOList;
    }

    public List<FavoriteRoomResponseDTO> getFavoriteRoomList(Member member) {
        List<Favorite> favoriteList = favoriteRepository.findByMemberAndFavoriteType(member,
            FavoriteType.ROOM);

        if (favoriteList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> roomIdFavoriteIdMap = favoriteList.stream()
            .collect(Collectors.toMap(Favorite::getTargetId, Favorite::getId));

        List<Long> favoriteRoomIdList = new ArrayList<>(roomIdFavoriteIdMap.keySet());

        List<Room> findFavoriteRoomList = roomRepository.findAllById(favoriteRoomIdList);
        Map<Boolean, List<Room>> partitionedRoomsMap = findFavoriteRoomList.stream()
            .collect(Collectors.partitioningBy(room -> room.getStatus().equals(RoomStatus.ENABLE)));

        List<Room> enableFavoriteRoomList = partitionedRoomsMap.get(true);
        List<Room> notEnableFavoriteRoomList = partitionedRoomsMap.get(false);

        Map<Long, List<Mate>> roomIdMatesMap = notEnableFavoriteRoomList.stream().collect(
            Collectors.toMap(Room::getId, mateRepository::findFetchMemberByRoom));

        List<String> criteriaPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        MemberStat memberStat = member.getMemberStat();

        List<FavoriteRoomResponseDTO> favoriteRoomResponseList = notEnableFavoriteRoomList.stream()
            .map(room -> {
                List<Mate> mates = roomIdMatesMap.get(room.getId());

                List<PreferenceMatchCountDTO> preferenceStatsMatchCountList = getPreferenceStatsMatchCounts(
                    member, criteriaPreferenceList, mates, memberStat);

                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(
                    member.getId(), mates.stream().map(mate -> mate.getMember().getId())
                        .collect(Collectors.toList()));

                Integer roomEquality = getCalculateRoomEquality(member.getId(), equalityMap);

                List<String> roomHashTags = roomHashtagRepository.findHashtagsByRoomId(
                    room.getId());

                return FavoriteConverter.toFavoriteRoomResponseDTO(
                    roomIdFavoriteIdMap.get(room.getId()), room, roomEquality,
                    preferenceStatsMatchCountList, roomHashTags, mates.size()
                );
            })
            .toList();

        // 활성화된 방이 존재한다면 찜에서 삭제
        if (!enableFavoriteRoomList.isEmpty()) {
            List<Long> roomIds = enableFavoriteRoomList.stream()
                .map(Room::getId)
                .toList();
            favoriteRepository.deleteAllByTargetIdsAndFavoriteType(roomIds, FavoriteType.ROOM);
        }

        return favoriteRoomResponseList;
    }

    private List<PreferenceMatchCountDTO> getPreferenceStatsMatchCounts(Member member,
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

    private Integer getCalculateRoomEquality(Long memberId, Map<Long, Integer> equalityMap) {
        List<Integer> roomEquality = equalityMap.values().stream()
            .toList();

        return (int) Math.round(roomEquality.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse((Integer)null));
    }
}