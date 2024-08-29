package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.converter.NotificationLogConverter;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogResponseDto;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationLogQueryService {

    private final NotificationLogRepository notificationLogRepository;

    public List<NotificationLogResponseDto> getNotificationLogList(Member member,
        NotificationCategory notificationCategory) {
        List<NotificationLog> notificationLogs;
        if (notificationCategory != null) {
            notificationLogs = notificationLogRepository.findByMemberAndCategoryOrderByIdDesc(
                member, notificationCategory);
        } else {
            notificationLogs = notificationLogRepository.findByMemberOrderByIdDesc(member);
        }

        List<NotificationLogResponseDto> notificationLogResponseDtoList = notificationLogs.stream()
            .map(notificationLog -> NotificationLogConverter.toResponseDto(
                notificationLog.getContent(), notificationLog.getCreatedAt()))
            .toList();

        return notificationLogResponseDtoList;
    }
}