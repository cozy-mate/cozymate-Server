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

    private static final List<String> patterns = List.of("으로 나를 초대했어요", "님에게 방 참여 요청을 보냈어요");
    private static final List<String> categoryList = List.of(
        NotificationCategory.ROOM_INVITE_REQUEST.toString(),
        NotificationCategory.ROOM_JOIN_REQUEST.toString());

    public void createNotificationLog(NotificationLog notificationLog) {
        notificationLogRepository.save(notificationLog);
    }

    public Slice<NotificationLog> getNotificationLogListByMember(Member member, Pageable pageable) {
        return notificationLogRepository.findByMemberAndCategoryNotInOrderByIdDesc(
            member, List.of(NotificationCategory.COZY_HOME, NotificationCategory.COZY_ROLE),
            pageable);
    }

    public void createNoticeNotificationLog(List<Long> successMemberIdList, String content,
        Long targetId) {
        notificationLogBulkRepository.saveAll(successMemberIdList, NotificationCategory.NOTICE,
            content, targetId);
    }

    public void deleteNotificationLogByMemberId(Long memberId) {
        notificationLogRepository.deleteAllByMemberId(memberId);
        updateNotificationLogTargetIdToNullByMemberId(memberId);
    }

    public void updateNotificationLogTargetIdToNullByRoomId(Long roomId) {
        notificationLogRepository.updateTargetIdToNullByRoomId(roomId, categoryList,
            patterns.get(0), patterns.get(1));
    }

    private void updateNotificationLogTargetIdToNullByMemberId(Long memberId) {
        notificationLogRepository.updateTargetIdToNullByMemberId(memberId, categoryList,
            patterns.get(0), patterns.get(1));
    }
}
