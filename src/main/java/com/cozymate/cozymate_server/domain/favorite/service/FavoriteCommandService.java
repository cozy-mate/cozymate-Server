package com.cozymate.cozymate_server.domain.favorite.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.converter.FavoriteConverter;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteRequestDto;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
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

    public void saveFavorite(Member member, FavoriteRequestDto favoriteRequestDto) {
        Long targetId = favoriteRequestDto.getTargetId();
        FavoriteType favoriteType = FavoriteType.valueOf(favoriteRequestDto.getFavoriteType());

        if (favoriteType.equals(FavoriteType.MEMBER)) {
            checkSelfFavorite(member, favoriteRequestDto);
        }

        checkTargetExists(favoriteType, targetId);
        checkDuplicateFavorite(member, targetId, favoriteType);

        favoriteRepository.save(FavoriteConverter.toEntity(member, targetId, favoriteType));
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

    private static void checkSelfFavorite(Member member, FavoriteRequestDto favoriteRequestDto) {
        if (favoriteRequestDto.getTargetId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._FAVORITE_CANNOT_REQUEST_SELF);
        }
    }

    private void checkTargetExists(FavoriteType favoriteType, Long targetId) {
        if (favoriteType.equals(FavoriteType.MEMBER)) {
            memberRepository.findById(targetId).orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
            );
        } else {
            roomRepository.findById(targetId).orElseThrow(
                () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND)
            );
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