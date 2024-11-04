package com.cozymate.cozymate_server.domain.favorite.repository;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByMemberAndTargetIdAndFavoriteType(Member member, Long targetId,
        FavoriteType favoriteType);

    List<Favorite> findByMemberAndFavoriteType(Member member, FavoriteType favoriteType);
}