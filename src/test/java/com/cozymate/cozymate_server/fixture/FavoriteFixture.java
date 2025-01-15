package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;

public class FavoriteFixture {

    private static final Long FAVORITE_ID_1 = 1L;
    private static final Long FAVORITE_ID_2 = 2L;


    public static Favorite buildMemberFavorite(Member member, Member targetMember) {
        return Favorite.builder()
            .id(FAVORITE_ID_1)
            .member(member)
            .targetId(targetMember.getId())
            .favoriteType(FavoriteType.MEMBER)
            .build();
    }

    public static Favorite buildRoomFavorite(Member member, Room targetRoom) {
        return Favorite.builder()
            .id(FAVORITE_ID_2)
            .member(member)
            .targetId(targetRoom.getId())
            .favoriteType(FavoriteType.ROOM)
            .build();
    }
}
