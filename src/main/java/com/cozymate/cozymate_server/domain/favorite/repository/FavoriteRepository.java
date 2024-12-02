package com.cozymate.cozymate_server.domain.favorite.repository;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByMemberAndTargetIdAndFavoriteType(Member member, Long targetId,
        FavoriteType favoriteType);

    List<Favorite> findByMemberAndFavoriteType(Member member, FavoriteType favoriteType);

    Optional<Favorite> findByMemberAndTargetIdAndFavoriteType(Member member, Long targetId,
        FavoriteType favoriteType);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying(clearAutomatically = true)
    @Query("delete from Favorite f where f.targetId in :targetIds and f.favoriteType = :favoriteType")
    void deleteAllByTargetIdsAndFavoriteType(@Param("targetIds") List<Long> targetIds,
        @Param("favoriteType") FavoriteType favoriteType);

    void deleteByTargetIdAndFavoriteType(@Param("targetId") Long targetId,
        @Param("favoriteType") FavoriteType favoriteType);

    void deleteByMemberId(Long memberId);

}