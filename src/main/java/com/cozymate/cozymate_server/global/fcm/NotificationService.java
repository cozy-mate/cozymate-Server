package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetDto.GroupTargetDto;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetDto.OneTargetReverseDto;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetDto.OneTargetDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String NOTIFICATION_TITLE = "cozymate";
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationLogRepository notificationLogRepository;

    @Async
    public void sendNotification(OneTargetDto target) {
        Member member = target.getMember();

        if (target.getTodoContents() != null) {
            sendNotificationToMember(member, target.getNotificationType(),
                target.getTodoContents());
        } else if (target.getRoleContent() != null) {
            sendNotificationToMember(member, target.getNotificationType(),
                target.getRoleContent());
        } else {
            sendNotificationToMember(member, target.getNotificationType());
        }
    }

    @Async
    public void sendNotification(OneTargetReverseDto target) {
        Member contentMember = target.getContentMember();
        Member recipientMember = target.getRecipientMember();

        sendNotificationToMember(contentMember, recipientMember, target.getNotificationType());
    }

    @Async
    public void sendNotification(GroupTargetDto target) {
        List<Member> memberList = target.getMemberList();

        memberList.forEach(member -> {
            sendNotificationToMember(member,
                target.getNotificationType());
        });
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType) {
        Message message = createMessage(member, notificationType);

        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(member)
                .category(notificationType.getCategory())
                .content(getContent(member, notificationType))
                .build();

            notificationLogRepository.save(notificationLog);

        } catch (FirebaseMessagingException e) {
            log.error("cannot send to member push message. error info : {}", e.getMessage());
        }
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType, List<String> todoContents) {
        Message message = createMessage(member, notificationType, todoContents);
        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(member)
                .category(notificationType.getCategory())
                .content(getContent(member, notificationType, todoContents))
                .build();

            notificationLogRepository.save(notificationLog);

        } catch (FirebaseMessagingException e) {
            log.error("cannot send to member push message. error info : {}", e.getMessage());
        }
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType, String roleContent) {
        Message message = createMessage(member, notificationType, roleContent);

        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(member)
                .category(notificationType.getCategory())
                .content(getContent(member, notificationType, roleContent))
                .build();

            notificationLogRepository.save(notificationLog);

        } catch (FirebaseMessagingException e) {
            log.error("cannot send to member push message. error info : {}", e.getMessage());
        }
    }

    private void sendNotificationToMember(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        Message message = createMessage(contentMember, recipientMember, notificationType);
        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(recipientMember)
                .category(notificationType.getCategory())
                .content(getContent(contentMember, notificationType))
                .build();

            notificationLogRepository.save(notificationLog);

        } catch (FirebaseMessagingException e) {
            log.error("cannot send to member push message. error info : {}", e.getMessage());
        }
    }

    private Message createMessage(Member member, NotificationType notificationType) {
        String token = member.getFcmToken();
        String content = getContent(member, notificationType);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        return Message.builder()
            .putAllData(messageMap)
            .setToken(token)
            .build();
    }

    private Message createMessage(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        String token = recipientMember.getFcmToken();
        String content = getContent(contentMember, notificationType);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        return Message.builder()
            .putAllData(messageMap)
            .setToken(token)
            .build();
    }

    private Message createMessage(Member member, NotificationType notificationType,
        List<String> todoContents) {
        String token = member.getFcmToken();
        String content = getContent(member, notificationType, todoContents);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        return Message.builder()
            .putAllData(messageMap)
            .setToken(token)
            .build();
    }

    private Message createMessage(Member member, NotificationType notificationType,
        String roleContent) {
        String token = member.getFcmToken();
        String content = getContent(member, notificationType, roleContent);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        return Message.builder()
            .putAllData(messageMap)
            .setToken(token)
            .build();
    }

    private String getContent(Member member, NotificationType notificationType) {
        return notificationType.generateContent(NotificationContentDto.create(member));
    }

    private String getContent(Member member, NotificationType notificationType,
        String roleContent) {
        return notificationType.generateContent(NotificationContentDto.create(member, roleContent));
    }

    private String getContent(Member member, NotificationType notificationType,
        List<String> todoContents) {
        return notificationType.generateContent(
            NotificationContentDto.create(member, todoContents));
    }
}