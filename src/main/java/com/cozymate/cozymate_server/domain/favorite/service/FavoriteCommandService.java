package com.cozymate.cozymate_server.domain.favorite.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.converter.FavoriteConverter;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteCommandService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public void saveMemberFavorite(Member member, Long favoriteMemberId) {
        if (favoriteMemberId.equals(member.getId())) {
            throw new GeneralException(ErrorStatus._FAVORITE_CANNOT_REQUEST_SELF);
        }

        validTarget(FavoriteType.MEMBER, favoriteMemberId);
        checkDuplicateFavorite(member, favoriteMemberId, FavoriteType.MEMBER);

        favoriteRepository.save(FavoriteConverter.toEntity(member, favoriteMemberId, FavoriteType.MEMBER));
    }

    public void saveRoomFavorite(Member member, Long roomId) {
        validTarget(FavoriteType.ROOM, roomId);
        checkDuplicateFavorite(member, roomId, FavoriteType.ROOM);

        favoriteRepository.save(FavoriteConverter.toEntity(member, roomId, FavoriteType.ROOM));
    }

    public void deleteFavorite(Member member, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId).orElseThrow(
            () -> new GeneralException(ErrorStatus._FAVORITE_NOT_FOUND)
        );

        if (!favorite.getMember().getId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._FAVORITE_MEMBER_MISMATCH);
        };

        favoriteRepository.delete(favorite);
    }

    private void validTarget(FavoriteType favoriteType, Long targetId) {
        if (favoriteType.equals(FavoriteType.MEMBER)) {
            memberRepository.findById(targetId).orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
            );

        } else {
            Room room = roomRepository.findById(targetId).orElseThrow(
                () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND)
            );

            if (RoomType.PRIVATE.equals(room.getRoomType())) {
                throw new GeneralException(ErrorStatus._FAVORITE_CANNOT_PRIVATE_ROOM);
            }

            if (room.getNumOfArrival() == room.getMaxMateNum()) {
                throw new GeneralException(ErrorStatus._FAVORITE_CANNOT_FULL_ROOM);
            }

            if (RoomStatus.DISABLE.equals(room.getStatus())) {
                throw new GeneralException(ErrorStatus._FAVORITE_CANNOT_DISABLE_ROOM);
            }
        }
    }

    private void checkDuplicateFavorite(Member member, Long targetId, FavoriteType favoriteType) {
        boolean isExists = favoriteRepository.existsByMemberAndTargetIdAndFavoriteType(member,
            targetId, favoriteType);

        if (isExists) {
            throw new GeneralException(ErrorStatus._FAVORITE_ALREADY_EXISTS);
        }
    }
}