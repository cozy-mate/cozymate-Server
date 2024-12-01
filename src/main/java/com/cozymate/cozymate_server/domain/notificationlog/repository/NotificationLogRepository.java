package com.cozymate.cozymate_server.domain.notificationlog.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByMemberAndCategoryNotInOrderByIdDesc(Member member, List<NotificationCategory> categoryList);

    void deleteAllByMemberId(Long memberId);
}