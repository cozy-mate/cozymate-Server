package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogBulkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationLogCommandService {

    private final NotificationLogBulkRepository notificationLogBulkRepository;
    private final MemberRepository memberRepository;

    public void saveNoticeNotificationLog(String content, Long noticeId) {
        List<Long> memberIds = memberRepository.findAllMemberIds();
        notificationLogBulkRepository.saveAll(memberIds, NotificationCategory.NOTICE, content,
            noticeId);
    }
}