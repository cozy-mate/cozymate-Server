package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.converter.NotificationLogConverter;
import com.cozymate.cozymate_server.domain.notificationlog.dto.response.NotificationLogResponseDTO;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepositoryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationLogQueryService {

    private final NotificationLogRepositoryService notificationLogRepositoryService;

    public PageResponseDto<List<NotificationLogResponseDTO>> getNotificationLogList(Member member,
        int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Slice<NotificationLog> notificationLogList = notificationLogRepositoryService.getNotificationLogListByMember(
            member, pageRequest);

        List<NotificationLogResponseDTO> notificationLogResponseDTOList = notificationLogList.stream()
            .map(notificationLog -> NotificationLogConverter.toNotificationLogResponseDTO(
                notificationLog.getContent(), notificationLog.getCreatedAt(),
                notificationLog.getCategory().getName(), notificationLog.getTargetId()
            ))
            .toList();

        return PageResponseDto.<List<NotificationLogResponseDTO>>builder()
            .page(page)
            .hasNext(notificationLogList.hasNext())
            .result(notificationLogResponseDTOList)
            .build();
    }
}