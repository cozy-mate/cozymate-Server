package com.cozymate.cozymate_server.domain.memberfavorite.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberFavoriteRepository extends JpaRepository<MemberFavorite, Long> {

    boolean existsByMemberAndTargetMember(Member member, Member targetMember);

    List<MemberFavorite> findByMember(Member member);

    @Modifying
    @Query("delete from MemberFavorite mf where mf.member = :member or mf.targetMember = :member")
    void deleteByMemberOrTargetMember(@Param("member") Member member);
}
