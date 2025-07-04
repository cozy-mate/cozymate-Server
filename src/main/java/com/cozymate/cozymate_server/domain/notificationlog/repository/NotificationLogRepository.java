package com.cozymate.cozymate_server.domain.notificationlog.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    Slice<NotificationLog> findByMemberAndCategoryNotInOrderByIdDesc(Member member, List<NotificationCategory> categoryList, Pageable pageable);

    @Modifying
    @Query("delete from NotificationLog n where n.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
        update NotificationLog nl
        set nl.targetId = null
        where nl.targetId = :memberId
        and nl.category in :categoryList
        and (nl.content not like concat('%', :pattern1) and nl.content not like concat('%', :pattern2))
    """
    )
    void updateTargetIdToNullByMemberId(
        @Param("memberId") Long memberId, @Param("categoryList") List<String> categoryList,
        @Param("pattern1") String pattern1, @Param("pattern2") String pattern2
    );

    @Modifying
    @Query("""
        update NotificationLog nl
        set nl.targetId = null
        where nl.targetId = :roomId
        and (nl.category = 'ROOM' or (nl.category in :categoryList and (nl.content like concat('%', :pattern1) or nl.content like concat('%', :pattern2))))
    """
    )
    void updateTargetIdToNullByRoomId(
        @Param("roomId") Long roomId, @Param("categoryList") List<String> categoryList,
        @Param("pattern1") String pattern1, @Param("pattern2") String pattern2
    );
}