package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.GroupRoomNameWithOutMeTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.GroupTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.GroupWithOutMeTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.HostAndMemberAndRoomTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.OneTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.OneTargetReverseDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.OneTargetReverseWithRoomNameDTO;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.fcm.dto.MessageResult;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final MessageUtil messageUtil;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmRepositoryService fcmRepositoryService;
    private final NotificationLogRepositoryService notificationLogRepositoryService;

    /**
     * 투두 role 리마인더 스케줄러에서 사용중
     * 알림 내용에 본인 닉네임이 들어가고 본인에게 오는 알림인 경우
     */
    @Async
    public void sendNotification(OneTargetDTO target) {
        Member member = target.member();
        NotificationType notificationType = target.notificationType();

        if (target.todoContents() != null) {
            sendNotificationToMember(messageUtil.createMessage(member, notificationType,
                target.todoContents()));
        } else if (target.roleContent() != null) {
            sendNotificationToMember(messageUtil.createMessage(member, notificationType,
                target.roleContent()));
        } else {
            sendNotificationToMember(
                messageUtil.createMessage(member, notificationType));
        }
    }

    /**
     * 자신의 오늘의 todo를 모두 완료하면 메이트들에게 알림
     */
    @Async
    public void sendNotification(GroupWithOutMeTargetDTO target) {
        List<Member> memberList = target.memberList();
        Member me = target.me();
        NotificationType notificationType = target.notificationType();

        memberList.forEach(member -> {
            sendNotificationToMember(
                messageUtil.createMessage(me, member, notificationType));
        });
    }

    /**
     * ex) xx님이 초대 요청을 거절했어요 REJECT_ROOM_INVITE
     * xx님이 방 초대 요청을 수락했어요 ACCEPT_ROOM_INVITE
     * 위 예시들은 방장만 받는 알림임. (방장이 누군가에게 우리방 오라고 초대 보냈는 요청에 대한 그 사람의 선택 알림)
     * (그 사람은 아무 self 알림도 안바듬)
     */
    @Async
    public void sendNotification(OneTargetReverseDTO target) {
        Member contentMember = target.contentMember();
        Member recipientMember = target.recipientMember();
        NotificationType notificationType = target.notificationType();

        if (target.chatContent() != null) {
            sendNotificationToMember(
                messageUtil.createMessage(contentMember, recipientMember, target.chatContent(),
                    notificationType, target.chatRoom()));
        } else {
            sendNotificationToMember(messageUtil.createMessage(contentMember, recipientMember,
                notificationType));
        }
    }

    /**
     * ex) 더기님에게 xx방으로 초대 요청을 보냈어요 SEND_ROOM_INVITE <-> 델로님이 xx방으로 나를 초대했어요 ARRIVE_ROOM_INVITE
     */
    @Async
    public void sendNotification(HostAndMemberAndRoomTargetDTO target) {
        Member host = target.host();
        Member member = target.member();
        Room room = target.room();
        NotificationType hostNotificationType = target.hostNotificationType();
        NotificationType memberNotificationType = target.memberNotificationType();

        sendNotificationToMember(
            messageUtil.createMessage(member, room, host, hostNotificationType));

        sendNotificationToMember(
            messageUtil.createMessage(host, room, member, memberNotificationType));
    }

    /**
     * ex) xx님이 xx을/를 뛰쳐나갔어요! ROOM_OUT
     * xx님이 xx에 뛰어들어왔요! ROOM_IN
     */
    @Async
    public void sendNotification(GroupRoomNameWithOutMeTargetDTO target) {
        List<Member> memberList = target.memberList(); // 알림을 받을 멤버 리스트
        Member me = target.me(); // 알림 내용에 들어갈 멤버 (의 이름)
        Room room = target.room(); // 알림 내용에 들어갈 방 (의 이름)
        NotificationType notificationType = target.notificationType();

        memberList.forEach(member -> {
            sendNotificationToMember(
                messageUtil.createMessage(me, room, member, notificationType));
        });
    }

    @Async
    public void sendNotification(GroupTargetDTO target) {
        List<Member> memberList = target.memberList();
        NotificationType notificationType = target.notificationType();

        memberList.forEach(member -> {
            sendNotificationToMember(
                messageUtil.createMessage(member, notificationType));
        });
    }

    @Async
    public void sendNotification(OneTargetReverseWithRoomNameDTO target) {
        Member contentMember = target.contentMember();
        Member recipientMember = target.recipientMember();
        Room room = target.room();
        NotificationType notificationType = target.notificationType();

        sendNotificationToMember(
            messageUtil.createMessage(contentMember, room, recipientMember, notificationType));
    }

    private void sendNotificationToMember(MessageResult messageResult) {
        List<Message> messages = messageResult.getMessages();

        if (messages.isEmpty()) {
            return;
        }

        BatchResponse batchResponse = sendMessageToFirebase(messages,
            messageResult.getMessageTokenMap());

        if (batchResponse != null && batchResponse.getSuccessCount() > 0) {
            log.info("알림 전송 시도 갯수: {}", messages.size());
            log.info("알림 전송 성공 갯수: {}", batchResponse.getSuccessCount());

            NotificationLog notificationLog = messageResult.getNotificationLog();
            notificationLogRepositoryService.createNotificationLog(notificationLog);

            log.info("{}의 알림 로그 저장", notificationLog.getMember().getNickname());
        }
    }

    private BatchResponse sendMessageToFirebase(List<Message> messages,
        Map<Message, String> messageTokenMap) {
        BatchResponse batchResponse = null;
        try {
            batchResponse = firebaseMessaging.sendEach(messages);

            List<SendResponse> responses = batchResponse.getResponses();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);

                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();

                    if (exception != null) {
                        Message message = messages.get(i);
                        String token = messageTokenMap.get(message);
                        MessagingErrorCode errorCode = exception.getMessagingErrorCode();
                        log.error("알림 전송 실패 - 토큰: {}, 에러 코드: {}", token, errorCode);

                        if (errorCode == MessagingErrorCode.UNREGISTERED) {
                            fcmRepositoryService.updateFcmValidToFalseByToken(token); // 해당 토큰을 비활성화
                            log.info("비활성화된 FCM 토큰: {}", token);
                        }
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM 에러 코드: {}, 에러 메시지: {}", e.getMessagingErrorCode(), e.getMessage());
        }

        return batchResponse;
    }

    /**
     * 공지사항 알림용 토큰 최대 500개씩 나누어 호출
     * TODO: 아래 코드들은 호출 방식이 정해지지 않아 리팩토링에서 제외
     */
    @Async
    public void sendMulticastNotification(List<Fcm> fcmList, String content, Long targetId) {
        log.info("FcmPushService에서 현재 스레드 이름 (시작): {}", Thread.currentThread().getName());
        List<String> fcmTokenList = fcmList.stream()
            .map(Fcm::getToken)
            .toList();

        sendNotificationToMember(fcmList, content, targetId, fcmTokenList);
        log.info("FcmPushService에서 현재 스레드 이름 (종료): {}", Thread.currentThread().getName());
    }

    private void sendNotificationToMember(List<Fcm> fcmList, String content, Long targetId,
        List<String> fcmTokenList) {
        MulticastMessage message = messageUtil.createMessage(fcmTokenList, content);
        Set<String> failedTokenSet = sendMulticastMessageToFirebase(fcmTokenList, message);

        List<Long> successMemberIdList = fcmList.stream()
            .filter(fcm -> !failedTokenSet.contains(fcm.getToken()))
            .map(Fcm::getMember)
            .map(Member::getId)
            .distinct()
            .toList();

        if (!successMemberIdList.isEmpty()) {
            notificationLogRepositoryService.createNoticeNotificationLog(successMemberIdList,
                content, targetId);
            log.info("공지 사항 알림 내역 저장 완료");
        }
    }

    private Set<String> sendMulticastMessageToFirebase(List<String> fcmTokenList,
        MulticastMessage message) {
        Set<String> failedTokenSet = new HashSet<>();
        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(message);
            List<SendResponse> responses = batchResponse.getResponses();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);

                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();

                    if (exception != null) {
                        String token = fcmTokenList.get(i);
                        MessagingErrorCode errorCode = exception.getMessagingErrorCode();
                        failedTokenSet.add(token); // 실패 토큰 set에 추가
                        log.error("알림 전송 실패 - 토큰: {}, 에러 코드: {}", token, errorCode);

                        if (errorCode == MessagingErrorCode.UNREGISTERED) {
                            fcmRepositoryService.updateFcmValidToFalseByToken(token); // 해당 토큰을 비활성화
                            log.info("비활성화된 FCM 토큰: {}", token);
                        }
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM 에러 코드: {}, 에러 메시지: {}", e.getMessagingErrorCode(), e.getMessage());
        }

        return failedTokenSet;
    }
}