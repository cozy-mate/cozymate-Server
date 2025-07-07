package com.cozymate.cozymate_server.domain.sqs.service;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.push.content.FcmPushContentDTO;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.sqs.dto.FcmSQSMessage;
import com.cozymate.cozymate_server.domain.sqs.dto.SQSMessageResult;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogCreateDTO;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SQSMessageCreator {

    private final FcmRepository fcmRepository;

    private static final String NOTIFICATION_TITLE = "cozymate";

    /**
     * ROOM_IN
     * ROOM_OUT
     */
    public SQSMessageResult create(Member notRoomMember, Member roomMember,
        Room room, NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(roomMember);

        String notificationContent = getContent(notRoomMember, room, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(roomMember, notRoomMember,
                room, notificationContent));

        if (fcmList.isEmpty()) {
            return getEmptySQSMessageResult(notificationLog);
        }

        return getSQSMessageResult(fcmList, notificationContent, notificationType,
            notificationLog);
    }

    /**
     * ACCEPT_ROOM_INVITE
     * ARRIVE_ROOM_JOIN_REQUEST
     * ACCEPT_ROOM_JOIN
     */
    public SQSMessageResult create(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipientMember);

        String notificationContent = getContent(contentMember, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(recipientMember, contentMember,
                notificationContent));

        if (fcmList.isEmpty()) {
            return getEmptySQSMessageResult(notificationLog);
        }

        return getSQSMessageResult(fcmList, notificationContent, notificationType,
            notificationLog);
    }

    /**
     * ARRIVE_ROOM_INVITE
     * REJECT_ROOM_JOIN
     */
    public SQSMessageResult createWithRoomId(Member inviter, Member invitee, Room room,
        NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(invitee);

        String notificationContent = getContent(inviter, room, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(invitee, inviter,
                room, notificationContent));

        if (fcmList.isEmpty()) {
            return getEmptySQSMessageResult(notificationLog);
        }

        return getSQSMessageResultWithRoomId(fcmList, notificationContent, room, notificationType,
            notificationLog);
    }

    /**
     * REJECT_ROOM_INVITE
     */
    public SQSMessageResult createWithMemberId(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipientMember);

        String notificationContent = getContent(contentMember, notificationType);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(recipientMember, contentMember,
                notificationContent));

        if (fcmList.isEmpty()) {
            return getEmptySQSMessageResult(notificationLog);
        }

        return getSQSMessageResultWithMemberId(fcmList, notificationContent, contentMember,
            notificationType, notificationLog);
    }

    /**
     * ARRIVE_CHAT
     */
    public SQSMessageResult createWithChatRoomId(Member sender, Member recipient,
        String chatContent, ChatRoom chatRoom, NotificationType notificationType) {
        List<Fcm> fcmList = getFcmList(recipient);

        String notificationContent = getContent(sender, notificationType, chatContent);

        NotificationLog notificationLog = notificationType.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(recipient, sender,
                notificationContent, chatRoom));

        if (fcmList.isEmpty()) {
            return getEmptySQSMessageResult(notificationLog);
        }

        return getMessageResultWithChatRoomId(fcmList, notificationContent, notificationType,
            notificationLog, chatRoom);
    }


    private String getContent(Member member, Room room, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDTO.create(member, room));
    }

    private String getContent(Member member, NotificationType notificationType) {
        return notificationType.generateContent(FcmPushContentDTO.create(member));
    }

    private String getContent(Member member, NotificationType notificationType,
        String chatContent) {
        return notificationType.generateContent(FcmPushContentDTO.create(member, chatContent));
    }

    private SQSMessageResult getSQSMessageResult(List<Fcm> fcmList, String content,
        NotificationType notificationType, NotificationLog notificationLog) {

        List<FcmSQSMessage> fcmSQSMessageList = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                FcmSQSMessage fcmSqsMessage = FcmSQSMessage.builder()
                    .title(NOTIFICATION_TITLE)
                    .body(content)
                    .actionType(String.valueOf(notificationType))
                    .deviceToken(token)
                    .build();

                return fcmSqsMessage;
            }).toList();

        SQSMessageResult sqsMessageResult = SQSMessageResult.builder()
            .fcmSQSMessageList(fcmSQSMessageList)
            .notificationLog(notificationLog)
            .build();

        return sqsMessageResult;
    }

    private SQSMessageResult getSQSMessageResultWithRoomId(List<Fcm> fcmList, String content,
        Room room, NotificationType notificationType, NotificationLog notificationLog) {

        List<FcmSQSMessage> fcmSQSMessageList = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                FcmSQSMessage fcmSqsMessage = FcmSQSMessage.builder()
                    .title(NOTIFICATION_TITLE)
                    .body(content)
                    .actionType(String.valueOf(notificationType))
                    .deviceToken(token)
                    .roomId(room.getId().toString())
                    .build();

                return fcmSqsMessage;
            }).toList();

        SQSMessageResult sqsMessageResult = SQSMessageResult.builder()
            .fcmSQSMessageList(fcmSQSMessageList)
            .notificationLog(notificationLog)
            .build();

        return sqsMessageResult;
    }

    private SQSMessageResult getSQSMessageResultWithMemberId(List<Fcm> fcmList, String content,
        Member member, NotificationType notificationType, NotificationLog notificationLog) {

        List<FcmSQSMessage> fcmSQSMessageList = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                FcmSQSMessage fcmSqsMessage = FcmSQSMessage.builder()
                    .title(NOTIFICATION_TITLE)
                    .body(content)
                    .actionType(String.valueOf(notificationType))
                    .deviceToken(token)
                    .memberId(member.getId().toString())
                    .build();

                return fcmSqsMessage;
            }).toList();

        SQSMessageResult sqsMessageResult = SQSMessageResult.builder()
            .fcmSQSMessageList(fcmSQSMessageList)
            .notificationLog(notificationLog)
            .build();

        return sqsMessageResult;
    }

    private SQSMessageResult getMessageResultWithChatRoomId(List<Fcm> fcmList, String content,
        NotificationType notificationType, NotificationLog notificationLog, ChatRoom chatRoom) {

        List<FcmSQSMessage> fcmSQSMessageList = fcmList.stream()
            .map(fcm -> {
                String token = fcm.getToken();

                FcmSQSMessage fcmSqsMessage = FcmSQSMessage.builder()
                    .title(NOTIFICATION_TITLE)
                    .body(content)
                    .actionType(String.valueOf(notificationType))
                    .deviceToken(token)
                    .chatRoomId(chatRoom.getId().toString())
                    .build();

                return fcmSqsMessage;
            }).toList();

        SQSMessageResult sqsMessageResult = SQSMessageResult.builder()
            .fcmSQSMessageList(fcmSQSMessageList)
            .notificationLog(notificationLog)
            .build();

        return sqsMessageResult;
    }

    private List<Fcm> getFcmList(Member member) {
        return fcmRepository.findByMemberAndIsValidIsTrue(member);
    }

    private SQSMessageResult getEmptySQSMessageResult(NotificationLog notificationLog) {
        return SQSMessageResult.builder()
            .fcmSQSMessageList(List.of())
            .notificationLog(notificationLog)
            .build();
    }
}
