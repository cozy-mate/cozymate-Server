package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.fcm.dto.MessageResult;
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

        if (NotificationType.ARRIVE_ROOM_INVITE.equals(notificationType)) {
            return getMessageResult(fcmList, content, room);
        }

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipientMember);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(contentMember, notificationType);

        if (NotificationType.ARRIVE_ROOM_JOIN_REQUEST.equals(notificationType)) {
            return getMessageResult(fcmList, content, contentMember);
        }

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        List<String> todoContents) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType, todoContents);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        String roleContent) {
        List<Fcm> fcmList = getFcmList(member);

        if (fcmList.isEmpty()) {
            return getEmptyMessageResult();
        }

        String content = getContent(member, notificationType, roleContent);

        return getMessageResult(fcmList, content);
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
        return notificationType.generateContent(FcmPushContentDto.create(member));
    }

    private String getContent(Member member, NotificationType notificationType,
        String roleContent) {
        return notificationType.generateContent(FcmPushContentDto.create(member, roleContent));
    }

    private String getContent(Member member, NotificationType notificationType,
        List<String> todoContents) {
        return notificationType.generateContent(FcmPushContentDto.create(member, todoContents));
    }

    private String getContent(Member member, Room room, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDto.create(member, room));
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content, Member member) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);
                messageMap.put("memberId", member.getId().toString());

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
            .content(content)
            .build();
        return messageResult;
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content, Room room) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);
                messageMap.put("roomId", room.getId().toString());

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
            .content(content)
            .build();
        return messageResult;
    }

    private MessageResult getMessageResult(List<Fcm> fcmList, String content) {
        Map<Message, String> messageTokenMap = new HashMap<>();

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

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
            .content(content)
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