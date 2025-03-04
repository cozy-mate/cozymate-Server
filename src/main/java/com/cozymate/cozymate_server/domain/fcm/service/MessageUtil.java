package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.push.content.FcmPushContentDTO;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.fcm.dto.MessageResult;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogCreateDTO;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private static final String NOTIFICATION_TITLE = "cozymate";
    private final FcmRepository fcmRepository;

    public MessageResult createMessage(Member contentMember, Room room,
        Member recipientMember, NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipientMember);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(contentMember, room, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(recipientMember, contentMember,
                room, content));

        if (NotificationType.ARRIVE_ROOM_INVITE.equals(notificationType)) {
            return getMessageResult(fcmList, content, room, notificationType, notificationLog);
        }

        return getMessageResult(fcmList, content, notificationType, notificationLog);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(member, content));

        return getMessageResult(fcmList, content, notificationType, notificationLog);
    }

    public MessageResult createMessage(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipientMember);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(contentMember, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(recipientMember, contentMember,
                content));

        return getMessageResult(fcmList, content, notificationType, notificationLog);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        List<String> todoContents) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType, todoContents);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(member, content));

        return getMessageResult(fcmList, content, notificationType, notificationLog);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        String roleContent) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType, roleContent);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(member, content));

        return getMessageResult(fcmList, content, notificationType, notificationLog);
    }

    public MulticastMessage createMessage(List<String> fcmTokenValueList, String content) {
        HashMap<String, String> messageMap = new HashMap<>();

        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        Notification notification = Notification.builder()
            .setTitle(NOTIFICATION_TITLE)
            .setBody(content)
            .build();

        return MulticastMessage.builder()
            .putAllData(messageMap)
            .setNotification(notification)
            .addAllTokens(fcmTokenValueList)
            .build();
    }

    private String getContent(Member member, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDTO.create(member));
    }

    private String getContent(Member member, NotificationType notificationType,
        String roleContent) {
        return notificationType.generateContent(FcmPushContentDTO.create(member, roleContent));
    }

    private String getContent(Member member, NotificationType notificationType,
        List<String> todoContents) {
        return notificationType.generateContent(FcmPushContentDTO.create(member, todoContents));
    }

    private String getContent(Member member, Room room, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDTO.create(member, room));
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content, Member member,
        NotificationType notificationType) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);
                messageMap.put("memberId", member.getId().toString());
                messageMap.put("actionType", String.valueOf(notificationType));

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                Message message = Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();

                messageTokenMap.put(message, token);

                return message;
            }).toList();

        MessageResult messageResult = MessageResult.builder()
            .messageTokenMap(messageTokenMap)
            .messages(messages)
            .build();
        return messageResult;
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content, Room room,
        NotificationType notificationType, NotificationLog notificationLog) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);
                messageMap.put("roomId", room.getId().toString());
                messageMap.put("actionType", String.valueOf(notificationType));

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                Message message = Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();

                messageTokenMap.put(message, token);

                return message;
            }).toList();

        MessageResult messageResult = MessageResult.builder()
            .messageTokenMap(messageTokenMap)
            .messages(messages)
            .notificationLog(notificationLog)
            .build();
        return messageResult;
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content,
        NotificationType notificationType, NotificationLog notificationLog) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);
                messageMap.put("actionType", String.valueOf(notificationType));

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                Message message = Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();

                messageTokenMap.put(message, token);

                return message;
            }).toList();

        MessageResult messageResult = MessageResult.builder()
            .messageTokenMap(messageTokenMap)
            .messages(messages)
            .notificationLog(notificationLog)
            .build();
        return messageResult;
    }

    private List<Fcm> getFcmList(Member member) {
        return fcmRepository.findByMemberAndIsValidIsTrue(member);
    }

    private MessageResult getEmptyMessageResult() {
        return MessageResult.builder()
            .messages(new ArrayList<>())
            .build();
    }
}