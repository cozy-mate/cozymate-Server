package com.cozymate.cozymate_server.domain.notificationlog.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByMemberAndCategoryNotInOrderByIdDesc(Member member, List<NotificationCategory> categoryList);

    @Modifying
    @Query("delete from NotificationLog n where n.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}