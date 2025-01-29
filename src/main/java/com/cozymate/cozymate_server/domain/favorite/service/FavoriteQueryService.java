package com.cozymate.cozymate_server.domain.favorite.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.converter.FavoriteConverter;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteMemberResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteRoomResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;

    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;

    public List<FavoriteMemberResponseDTO> getFavoriteMemberList(Member member) {
        List<Favorite> favoriteList = favoriteRepository.findByMemberAndFavoriteType(
            member, FavoriteType.MEMBER);

        if (favoriteList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> memberIdFavoriteIdMap = favoriteList.stream()
            .collect(Collectors.toMap(Favorite::getTargetId, Favorite::getId));

        List<Long> findFavoriteMemberIdList = new ArrayList<>(memberIdFavoriteIdMap.keySet());

        List<Member> existFavoriteMemberList = memberRepository.findAllById(
            findFavoriteMemberIdList);

        Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(member.getId(),
            existFavoriteMemberList.stream().map(Member::getId).toList());

        List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        MemberStat memberStat = member.getMemberStat();

        List<FavoriteMemberResponseDTO> favoriteMemberResponseDTOList = existFavoriteMemberList.stream()
            .filter(favoriteMember -> Objects.nonNull(favoriteMember.getMemberStat()))
            .map(favoriteMember -> {
                    if (Objects.isNull(memberStat)) {
                        return FavoriteConverter.toFavoriteMemberResponseDTO(
                            memberIdFavoriteIdMap.get(favoriteMember.getId()),
                            MemberStatConverter.toPreferenceResponseDTO(
                                favoriteMember.getMemberStat(),
                                MemberStatConverter.toMemberStatPreferenceDetailWithoutColorDTOList(
                                    favoriteMember.getMemberStat(), criteriaPreferences), null));
                    }
                    return FavoriteConverter.toFavoriteMemberResponseDTO(
                        memberIdFavoriteIdMap.get(favoriteMember.getId()),
                        MemberStatConverter.toPreferenceResponseDTO(
                            favoriteMember.getMemberStat(),
                            MemberStatConverter.toMemberStatPreferenceDetailColorDTOList(
                                favoriteMember.getMemberStat(), member.getMemberStat(),
                                criteriaPreferences
                            ), equalityMap.get(favoriteMember.getId())));
                }
            ).toList();

        // 탈퇴한 회원이 있다면 삭제 처리
        deleteFavoriteMember(findFavoriteMemberIdList, existFavoriteMemberList);

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

        List<Long> findFavoriteRoomIdList = new ArrayList<>(roomIdFavoriteIdMap.keySet());

        List<Room> existFavoriteRoomList = roomRepository.findAllById(findFavoriteRoomIdList);

        // 방 상태가 disable과 그 외로 분리
        Map<Boolean, List<Room>> partitionedRoomStatusMap = existFavoriteRoomList.stream()
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
                room -> mateRepository.findFetchMemberAndMemberStatByRoom(room, EntryStatus.JOINED)));

        // 로그인 사용자의 선호 스탯 4가지를 리스트로 가져온다
        List<String> criteriaPreferenceList = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        // 로그인 사용자의 member stat
        MemberStat memberStat = member.getMemberStat();

        List<FavoriteRoomResponseDTO> favoriteRoomResponseList = responseRoomList.stream()
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
                List<RoomHashtag> roomHashtags = room.getRoomHashtags();
                List<String> roomHashTags = roomHashtags.stream()
                    .map(RoomHashtag::getHashtag)
                    .map(Hashtag::getHashtag)
                    .toList();

                return FavoriteConverter.toFavoriteRoomResponseDTO(
                    roomIdFavoriteIdMap.get(room.getId()), room, roomEquality,
                    preferenceStatsMatchCountList, roomHashTags, mates.size()
                );
            })
            .toList();

        // 조회 조건에 맞지 않는 방 찜 삭제 처리
        deleteFavoriteRoom(existFavoriteRoomList, findFavoriteRoomIdList, partitionedRoomStatusMap,
            partitionedMateNumMap);

        return favoriteRoomResponseList;
    }

    private void deleteFavoriteMember(List<Long> findFavoriteMemberIdList,
        List<Member> existFavoriteMemberList) {
        Set<Long> existMemberIdSet = existFavoriteMemberList.stream()
            .map(Member::getId)
            .collect(Collectors.toSet());

        List<Long> deletedMemberIdList = findFavoriteMemberIdList.stream()
            .filter(id -> !existMemberIdSet.contains(id))
            .toList();

        favoriteRepository.deleteAllByTargetIdsAndFavoriteType(deletedMemberIdList,
            FavoriteType.MEMBER);
    }

    private void deleteFavoriteRoom(List<Room> existFavoriteRoomList,
        List<Long> findFavoriteRoomIdList,
        Map<Boolean, List<Room>> partitionedRoomStatusMap,
        Map<Boolean, List<Room>> partitionedMateNumMap) {
        // 찜한 방id 에서 실제 조회된 방들의 id
        Set<Long> existRoomIdSet = existFavoriteRoomList.stream()
            .map(Room::getId)
            .collect(Collectors.toSet());

        // 방이 조회 되지 않은 targetId(roomId)를 가지는 레코드 삭제를 위한 리스트 추출 (찜에서 삭제할거임)
        List<Long> deletedRoomIdList = findFavoriteRoomIdList.stream()
            .filter(id -> !existRoomIdSet.contains(id))
            .collect(Collectors.toList());

        // 방 status가 disable인 방 리스트, 존재한다면 방이 있다면 deletedRoomIdList에 추가
        List<Room> disableFavoriteRoomList = partitionedRoomStatusMap.get(false);
        if (!disableFavoriteRoomList.isEmpty()) {
            deletedRoomIdList.addAll(disableFavoriteRoomList.stream()
                .map(Room::getId)
                .toList());
        }

        // 인원이 가득 찬 방 리스트, 존재한다면 deletedRoomIdList에 추가
        List<Room> fullRoomList = partitionedMateNumMap.get(false);
        if (!fullRoomList.isEmpty()) {
            deletedRoomIdList.addAll(fullRoomList.stream()
                .map(Room::getId)
                .toList());
        }

        // deletedRoomIdList에 들어 있는 targetId(roomId)에 해당하는 방 찜 삭제 처리
        if (!deletedRoomIdList.isEmpty()) {
            favoriteRepository.deleteAllByTargetIdsAndFavoriteType(deletedRoomIdList,
                FavoriteType.ROOM);
        }
    }
}