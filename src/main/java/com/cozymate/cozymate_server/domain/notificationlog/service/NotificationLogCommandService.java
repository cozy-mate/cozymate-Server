package com.cozymate.cozymate_server.domain.notificationlog.service;

import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationLogCommandService {

    private final NotificationLogRepository notificationLogRepository;

    public void saveLog(NotificationLog notificationLog) {
        notificationLogRepository.save(notificationLog);
    }
}