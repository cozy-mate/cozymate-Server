package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;

@SuppressWarnings("NonAsciiCharacters")
public class FavoriteFixture {

    // 정상 더미데이터, 멤버 찜인 경우
    public static Favorite 정상_1(Member member, Member targetMember) {
        return Favorite.builder()
            .id(1L)
            .member(member)
            .targetId(targetMember.getId())
            .favoriteType(FavoriteType.MEMBER)
            .build();
    }

    // 정상 더미데이터, 방 찜인 경우
    public static Favorite 정상_2(Member member, Room targetRoom) {
        return Favorite.builder()
            .id(2L)
            .member(member)
            .targetId(targetRoom.getId())
            .favoriteType(FavoriteType.ROOM)
            .build();
    }

    // 에러 더미데이터, targetId가 null인 경우
    public static Favorite 값이_null인_targetId(Member member) {
        return Favorite.builder()
            .id(3L)
            .member(member)
            .targetId(null)
            .favoriteType(FavoriteType.MEMBER)
            .build();
    }
}
