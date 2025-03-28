package com.cozymate.cozymate_server.domain.notificationlog.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationLogRepositoryService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationLogBulkRepository notificationLogBulkRepository;

    public void createNotificationLog(NotificationLog notificationLog) {
        notificationLogRepository.save(notificationLog);
    }

    public Slice<NotificationLog> getNotificationLogListByMember(Member member, Pageable pageable) {
        return notificationLogRepository.findByMemberAndCategoryNotInOrderByIdDesc(
            member, List.of(NotificationCategory.COZY_HOME, NotificationCategory.COZY_ROLE), pageable);
    }

    public void createNoticeNotificationLog(List<Long> successMemberIdList, String content,
        Long targetId) {
        notificationLogBulkRepository.saveAll(successMemberIdList, NotificationCategory.NOTICE,
            content, targetId);
    }

    public void deleteNotificationLogByMemberId(Long memberId) {
        notificationLogRepository.deleteAllByMemberId(memberId);
    }
}
