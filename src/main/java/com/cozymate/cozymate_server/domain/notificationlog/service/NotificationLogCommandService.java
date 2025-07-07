package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationLogCommandService {

    private final NotificationLogRepositoryService notificationLogRepositoryService;

    public void updateTargetIdToNullByRoomId(Long roomId) {
        notificationLogRepositoryService.updateNotificationLogTargetIdToNullByRoomId(roomId);
    }
}
