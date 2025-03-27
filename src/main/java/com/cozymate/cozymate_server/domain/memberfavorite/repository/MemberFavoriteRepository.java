package com.cozymate.cozymate_server.domain.memberfavorite.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberFavoriteRepository extends JpaRepository<MemberFavorite, Long> {

    boolean existsByMemberAndTargetMember(Member member, Member targetMember);

    @Query("""
        select mf from MemberFavorite mf
        join fetch mf.targetMember tm
        join fetch tm.memberStat ms
        where mf.member = :member
    """)
    Slice<MemberFavorite> findPagingByMember(@Param("member") Member member, Pageable pageable);

    @Modifying
    @Query("delete from MemberFavorite mf where mf.member = :member or mf.targetMember = :member")
    void deleteByMemberOrTargetMember(@Param("member") Member member);

    @Query("select mf from MemberFavorite mf where mf.member = :viewer AND mf.targetMember.id = :targetMemberId")
    Optional<MemberFavorite> findByMemberAndTargetMember(@Param("viewer") Member viewer,
        @Param("targetMemberId") Long targetMemberId);
}
