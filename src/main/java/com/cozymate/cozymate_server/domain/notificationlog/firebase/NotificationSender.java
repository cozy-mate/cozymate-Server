package com.cozymate.cozymate_server.domain.notificationlog.firebase;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.GroupTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.OneTargetReverseVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.OneTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.firebase.NotificationTargetVO.TwoTargetVO;
import com.cozymate.cozymate_server.domain.notificationlog.service.NotificationLogCommandService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class NotificationSender {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final NotificationLogCommandService notificationLogCommandService;

    public NotificationLog sendNotification(OneTargetVO target) {
        Member member = findMember(target.getMemberId());

        if (target.getRoomName() == null) {
            return sendNotificationToMember(member,
                target.getNotificationType());
        } else {
            return sendNotificationToMember(member, target.getRoomName(),
                target.getNotificationType());
        }
    }

    public NotificationLog sendNotification(OneTargetReverseVO target) {
        Member me = findMember(target.getMyId());
        Member recipient = findMember(target.getRecipientId());

        return sendNotificationToMember(recipient, me, target.getNotificationType());
    }

    public List<NotificationLog> sendNotification(TwoTargetVO target) {
        Member recipient = findMember(target.getRecipientId());
        Member me = findMember(target.getMyId());

        NotificationLog notificationLog = sendNotificationToMember(recipient, me,
            target.getRecipientNotificationType());
        NotificationLog notificationLog2 = sendNotificationToMember(me, recipient,
            target.getMyNotificationType());

        return List.of(notificationLog, notificationLog2);
    }

    public List<NotificationLog> sendNotification(GroupTargetVO target) {
        List<Member> memberList = target.getMemberIdList().stream()
            .map(this::findMember).collect(Collectors.toList());

        return memberList.stream().map(member -> {
            NotificationLog notificationLog = sendNotificationToMember(member,
                target.getNotificationType());
            return notificationLog;
        }).collect(Collectors.toList());
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }

    private NotificationLog sendNotificationToMember(Member member,
        NotificationType notificationType) {
        Message message = createMessage(member, notificationType);

        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(member)
                .category(notificationType.getCategory())
                .content(getContent(member, notificationType))
                .build();

            notificationLogCommandService.saveLog(notificationLog);

            return notificationLog;
        } catch (FirebaseMessagingException e) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_FAILED);
        }
    }

    private NotificationLog sendNotificationToMember(Member member, String roomName,
        NotificationType notificationType) {
        Message message = createMessage(member, roomName, notificationType);

        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(member)
                .category(notificationType.getCategory())
                .content(getContent(roomName, notificationType))
                .build();

            notificationLogCommandService.saveLog(notificationLog);

            return notificationLog;
        } catch (FirebaseMessagingException e) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_FAILED);
        }
    }

    private NotificationLog sendNotificationToMember(Member recipient, Member sender,
        NotificationType notificationType) {
        Message message = createMessage(sender, notificationType);
        try {
            firebaseMessaging.send(message);

            NotificationLog notificationLog = NotificationLog.builder()
                .member(recipient)
                .category(notificationType.getCategory())
                .content(getContent(sender, notificationType))
                .build();

            notificationLogCommandService.saveLog(notificationLog);

            return notificationLog;
        } catch (FirebaseMessagingException e) {
            throw new GeneralException(ErrorStatus._NOTIFICATION_FAILED);
        }
    }

    private Message createMessage(Member member, NotificationType notificationType) {
        String token = member.getClientId();

        String content = getContent(member, notificationType);

        Notification notification = Notification.builder()
            .setTitle(null)
            .setBody(content)
            .setImage(null)
            .build();

        return Message.builder()
            .setNotification(notification)
            .setToken(token)
            .putData("notificationType", notificationType.toString())
            .build();
    }

    private Message createMessage(Member member, String roomName,
        NotificationType notificationType) {
        String token = member.getClientId();

        String content = getContent(roomName, notificationType);

        Notification notification = Notification.builder()
            .setTitle(null)
            .setBody(content)
            .setImage(null)
            .build();

        return Message.builder()
            .setNotification(notification)
            .setToken(token)
            .putData("notificationType", notificationType.toString())
            .build();
    }

    private String getContent(Member member, NotificationType notificationType) {
        return notificationType.generateContent(NotificationContentVO.create(member));
    }

    private String getContent(String roomName, NotificationType notificationType) {
        return notificationType.generateContent(NotificationContentVO.create(roomName));
    }
}