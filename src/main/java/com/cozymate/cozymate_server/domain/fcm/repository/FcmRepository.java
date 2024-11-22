package com.cozymate.cozymate_server.domain.fcm.repository;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FcmRepository extends JpaRepository<Fcm, String> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Fcm f set f.isValid = false where f.token = :token")
    void updateValidByToken(@Param("token") String token);

    List<Fcm> findByMemberAndIsValidIsTrue(Member member);

    List<Fcm> findAllByIsValidIsTrue();
}