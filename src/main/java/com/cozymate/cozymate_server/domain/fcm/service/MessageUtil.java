package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.fcm.dto.MessageResult;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private static final String NOTIFICATION_TITLE = "cozymate";
    private final FcmRepository fcmRepository;

    public MessageResult createMessage(Member contentMember, Room room,
        Member recipientMember, NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(recipientMember);

        if (fcmList.isEmpty()) {
            return MessageResult.builder()
                .messages(new ArrayList<>())
                .build();
        }

        String content = getContent(contentMember, room, notificationType);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(member);

        if (fcmList.isEmpty()) {
            return MessageResult.builder()
                .messages(new ArrayList<>())
                .build();
        }

        String content = getContent(member, notificationType);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(recipientMember);

        if (fcmList.isEmpty()) {
            return MessageResult.builder()
                .messages(new ArrayList<>())
                .build();
        }

        String content = getContent(contentMember, notificationType);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        List<String> todoContents) {
        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(member);

        if (fcmList.isEmpty()) {
            return MessageResult.builder()
                .messages(new ArrayList<>())
                .build();
        }

        String content = getContent(member, notificationType, todoContents);

        return getMessageResult(fcmList, content);
    }

    public MessageResult createMessage(Member member, NotificationType notificationType,
        String roleContent) {
        List<Fcm> fcmList = fcmRepository.findByMemberAndIsValidIsTrue(member);

        if (fcmList.isEmpty()) {
            return MessageResult.builder()
                .messages(new ArrayList<>())
                .build();
        }

        String content = getContent(member, notificationType, roleContent);

        return getMessageResult(fcmList, content);
    }

    public Message createMessage(String content, String topic) {
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("title", NOTIFICATION_TITLE);
        messageMap.put("body", content);

        Notification notification = Notification.builder()
            .setTitle(NOTIFICATION_TITLE)
            .setBody(content)
            .build();

        Message message = Message.builder()
            .putAllData(messageMap)
            .setTopic(topic)
            .setNotification(notification)
            .build();

        return message;
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

    private static MessageResult getMessageResult(List<Fcm> fcmList, String content) {
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
}