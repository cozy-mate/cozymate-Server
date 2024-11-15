package com.cozymate.cozymate_server.domain.inquiry.repository;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByMemberOrderByCreatedAtDesc(Member member);

    Boolean existsByMember(Member member);
}