package com.cozymate.cozymate_server.domain.inquiry.repository;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByMemberOrderByCreatedAtDesc(Member member);

    Boolean existsByMember(Member member);

    @Modifying
    @Query("UPDATE Inquiry i SET i.member = null WHERE i.member = :member")
    void bulkDeleteMember(@Param("member") Member member);
}