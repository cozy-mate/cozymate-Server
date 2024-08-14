package com.cozymate.cozymate_server.domain.fcm.repository;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmRepository extends JpaRepository<Fcm, String> {

    List<Fcm> findByMember(Member member);
}