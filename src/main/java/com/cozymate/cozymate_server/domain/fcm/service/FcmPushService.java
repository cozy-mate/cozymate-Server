package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushContentResultDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupRoomNameWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepository;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.room.Room;
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
public class FcmPushService {

    private static final String NOTIFICATION_TITLE = "cozymate";
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationLogRepository notificationLogRepository;
    private final FcmRepository fcmRepository;

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

    @Async
    public void sendNotification(GroupWithOutMeTargetDto target) {
        List<Member> memberList = target.getMemberList();
        Member me = target.getMe();

        memberList.forEach(member -> {
            sendNotificationToMember(me, member, target.getNotificationType());
        });
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType) {
        List<Message> messages = createMessage(member, notificationType);

        messages.forEach(message -> {
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to member push message. error info : {}", e.getMessage());
            }
        });

        NotificationLog notificationLog = NotificationLog.builder()
            .member(member)
            .category(notificationType.getCategory())
            .content(getContent(member, notificationType).getLogContent())
            .build();

        notificationLogRepository.save(notificationLog);
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType, List<String> todoContents) {
        List<Message> messages = createMessage(member, notificationType, todoContents);

        messages.forEach(message -> {
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to member push message. error info : {}", e.getMessage());
            }
        });

        NotificationLog notificationLog = NotificationLog.builder()
            .member(member)
            .category(notificationType.getCategory())
            .content(getContent(member, notificationType, todoContents).getLogContent())
            .build();

        notificationLogRepository.save(notificationLog);
    }

    private void sendNotificationToMember(Member member,
        NotificationType notificationType, String roleContent) {
        List<Message> messages = createMessage(member, notificationType, roleContent);

        messages.forEach(message -> {
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to member push message. error info : {}", e.getMessage());
            }
        });

        NotificationLog notificationLog = NotificationLog.builder()
            .member(member)
            .category(notificationType.getCategory())
            .content(getContent(member, notificationType, roleContent).getLogContent())
            .build();

        notificationLogRepository.save(notificationLog);
    }

    private void sendNotificationToMember(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Message> messages = createMessage(contentMember, recipientMember, notificationType);

        messages.forEach(message -> {
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to member push message. error info : {}", e.getMessage());
            }
        });

        NotificationLog notificationLog = NotificationLog.builder()
            .member(recipientMember)
            .category(notificationType.getCategory())
            .content(getContent(contentMember, notificationType).getLogContent())
            .build();

        notificationLogRepository.save(notificationLog);
    }

    private List<Message> createMessage(Member member, NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMember(member);

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();
                String content = getContent(member, notificationType).getNotificationContent();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                return Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            }).toList();

        return messages;
    }

    private List<Message> createMessage(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMember(recipientMember);

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();
                String content = getContent(contentMember,
                    notificationType).getNotificationContent();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                return Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            }).toList();

        return messages;
    }

    private List<Message> createMessage(Member member, NotificationType notificationType,
        List<String> todoContents) {
        List<Fcm> fcmList = fcmRepository.findByMember(member);

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();
                String content = getContent(member, notificationType,
                    todoContents).getNotificationContent();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                return Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            }).toList();

        return messages;
    }

    private List<Message> createMessage(Member member, NotificationType notificationType,
        String roleContent) {
        List<Fcm> fcmList = fcmRepository.findByMember(member);

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();
                String content = getContent(member, notificationType,
                    roleContent).getNotificationContent();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                return Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            }).toList();

        return messages;
    }

    private FcmPushContentResultDto getContent(Member member, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDto.create(member));
    }

    private FcmPushContentResultDto getContent(Member member, NotificationType notificationType,
        String roleContent) {
        return notificationType.generateContent(FcmPushContentDto.create(member, roleContent));
    }

    private FcmPushContentResultDto getContent(Member member, NotificationType notificationType,
        List<String> todoContents) {
        return notificationType.generateContent(
            FcmPushContentDto.create(member, todoContents));
    }


    /**
     * 방 입장 시 : 방에 속한 메이트 모두에게 [방이름]에 [닉네임]님이 뛰어들어왔어요! 알림 전송
     */
    @Async
    public void sendNotification(GroupRoomNameWithOutMeTargetDto target) {
        List<Member> memberList = target.getMemberList(); // 알림을 받을 멤버 리스트
        Member me = target.getMe(); // 알림 내용에 들어갈 멤버 (의 이름)
        Room room = target.getRoom(); // 알림 내용에 들어갈 방 (의 이름)

        memberList.forEach(member -> {
            sendNotificationToMember(me, room, member, target.getNotificationType());
        });
    }

    private void sendNotificationToMember(Member contentMember, Room room, Member recipientMember,
        NotificationType notificationType) {
        List<Message> messages = createMessage(contentMember, room, recipientMember,
            notificationType);

        messages.forEach(message -> {
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                log.error("cannot send to member push message. error info : {}", e.getMessage());
            }
        });

        NotificationLog notificationLog = NotificationLog.builder()
            .member(recipientMember)
            .category(notificationType.getCategory())
            .content(getContent(contentMember, room, notificationType).getLogContent())
            .build();

        notificationLogRepository.save(notificationLog);
    }

    private List<Message> createMessage(Member contentMember, Room room, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = fcmRepository.findByMember(recipientMember);

        List<Message> messages = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();
                String content = getContent(contentMember, room, notificationType).getNotificationContent();

                HashMap<String, String> messageMap = new HashMap<>();
                messageMap.put("title", NOTIFICATION_TITLE);
                messageMap.put("body", content);

                Notification notification = Notification.builder()
                    .setTitle(NOTIFICATION_TITLE)
                    .setBody(content)
                    .build();

                return Message.builder()
                    .putAllData(messageMap)
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            }).toList();

        return messages;
    }

    private FcmPushContentResultDto getContent(Member member, Room room,
        NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDto.create(member, room));
    }
}