package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.converter.NotificationLogConverter;
import com.cozymate.cozymate_server.domain.notificationlog.dto.response.NotificationLogResponseDTO;
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

    public List<NotificationLogResponseDTO> getNotificationLogList(Member member) {
        List<NotificationLog> notificationLogs = notificationLogRepository.findByMemberOrderByIdDesc(
            member);

        List<NotificationLogResponseDTO> notificationLogResponseDTOList = notificationLogs.stream()
            .map(notificationLog -> NotificationLogConverter.toNotificationLogResponseDTO(
                notificationLog.getContent(),
                notificationLog.getCreatedAt(),
                notificationLog.getCategory().getName(),
                notificationLog.getTargetId()
            ))
            .toList();

        return notificationLogResponseDTOList;
    }
}