package com.cozymate.cozymate_server.domain.fcm.event.listener;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomMemberRepository;
import com.cozymate.cozymate_server.global.websocket.repository.WebSocketSessionRepository;
import com.cozymate.cozymate_server.domain.fcm.event.SentChatEvent;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.fcm.dto.push.content.FcmPushContentDTO;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.JoinedRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.QuitRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RequestedJoinRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentMessageEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentInvitationEvent;
import com.cozymate.cozymate_server.domain.sqs.dto.FcmSQSMessage;
import com.cozymate.cozymate_server.domain.sqs.dto.SQSMessageResult;
import com.cozymate.cozymate_server.domain.sqs.service.SQSMessageSender;
import com.cozymate.cozymate_server.domain.sqs.service.SQSMessageCreator;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.NotificationLog;
import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogCreateDTO;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.notificationlog.repository.NotificationLogRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final MateRepository mateRepository;
    private final SQSMessageSender sqsMessageSender;
    private final SQSMessageCreator sqsMessageCreator;
    private final NotificationLogRepositoryService notificationLogRepositoryService;
    private final WebSocketSessionRepository webSocketSessionRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepositoryService memberRepositoryService;

    private static final int BATCH_SIZE = 200;

    @Async
    @TransactionalEventListener
    public void sendNotification(JoinedRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        // SQSMessageResult 리스트 생성
        List<SQSMessageResult> sqsMessageResultList = memberList.stream()
            .map(
                alreadyRoomMember -> sqsMessageCreator.create(member, alreadyRoomMember,
                    room, NotificationType.ROOM_IN))
            .toList();

        // NotificationLog 저장
        sqsMessageResultList
            .forEach(smr -> notificationLogRepositoryService.createNotificationLog(
                smr.notificationLog()));

        // sqs 전송
        sqsMessageResultList
            .stream()
            .filter(smr -> !smr.fcmSQSMessageList().isEmpty())
            .forEach(smr -> sqsMessageSender.sendMessage(smr.fcmSQSMessageList()));
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(QuitRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        // SQSMessageResult 리스트 생성
        List<SQSMessageResult> sqsMessageResultList = memberList.stream()
            .map(yetRoomMember -> sqsMessageCreator.create(member, yetRoomMember, room,
                NotificationType.ROOM_OUT))
            .toList();

        // NotificationLog 저장
        sqsMessageResultList
            .forEach(smr -> notificationLogRepositoryService.createNotificationLog(
                smr.notificationLog()));

        // sqs 전송
        sqsMessageResultList
            .stream()
            .filter(smr -> !smr.fcmSQSMessageList().isEmpty())
            .forEach(smr -> sqsMessageSender.sendMessage(smr.fcmSQSMessageList()));
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(SentInvitationEvent event) {
        Member inviter = event.inviter();
        Member invitee = event.invitee();
        Room room = event.room();

        // SQSMessage Result 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.createWithRoomId(inviter, invitee,
            room, NotificationType.ARRIVE_ROOM_INVITE);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(
            sqsMessageResult.notificationLog());

        // 본인은 알림 로그만 저장 (푸시 알림 전송 x)
        String content = NotificationType.SEND_ROOM_INVITE.generateContent(
            FcmPushContentDTO.create(invitee, room));
        NotificationLog notificationLog = NotificationType.SEND_ROOM_INVITE.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(inviter, invitee, room,
                content));
        notificationLogRepositoryService.createNotificationLog(notificationLog);

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(AcceptedInvitationEvent event) {
        Member invitee = event.invitee();
        Room room = event.room();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        // (방 초대 수락)
        // SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.create(invitee, inviter,
            NotificationType.ACCEPT_ROOM_INVITE);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(
            sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }

        // (방 입장)
        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(invitee.getId()))
            .toList();

        // SQSMessageResult 리스트 생성
        List<SQSMessageResult> sqsMessageResultList = memberList.stream()
            .map(alreadyRoomMember -> sqsMessageCreator.create(invitee,
                alreadyRoomMember, room, NotificationType.ROOM_IN))
            .toList();

        // NotificationLog 저장
        sqsMessageResultList
            .forEach(smr -> notificationLogRepositoryService.createNotificationLog(
                smr.notificationLog()));

        // sqs 전송
        sqsMessageResultList
            .stream()
            .filter(smr -> !smr.fcmSQSMessageList().isEmpty())
            .forEach(smr -> sqsMessageSender.sendMessage(smr.fcmSQSMessageList()));
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(RejectedInvitationEvent event) {
        Member invitee = event.invitee();
        Room room = event.room();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        // SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.createWithMemberId(invitee, inviter,
            NotificationType.REJECT_ROOM_INVITE);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(
            sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(RequestedJoinRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        Mate managerMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        Member managerMember = managerMate.getMember();

        // 방 참여 요청의 경우 요청자는 fcm 알림 전송 x, 알림 내역만 저장 (토스트 팝업 대체)
        String content = NotificationType.SENT_ROOM_JOIN_REQUEST.generateContent(
            FcmPushContentDTO.create(managerMember));
        NotificationLog notificationLog = NotificationType.SENT_ROOM_JOIN_REQUEST.generateNotificationLog(
            NotificationLogCreateDTO.createNotificationLogCreateDTO(member,
                managerMember, room, content));
        notificationLogRepositoryService.createNotificationLog(notificationLog);

        // SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.createWithMemberId(member,
            managerMember, NotificationType.ARRIVE_ROOM_JOIN_REQUEST);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(
            sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(AcceptedJoinEvent event) {
        Member manager = event.manager();
        Member requester = event.requester();
        Room room = event.room();

        // (방 참여 요청 수락)
        // SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.create(manager, requester,
            NotificationType.ACCEPT_ROOM_JOIN);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(
            sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }

        // (방 입장)
        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(requester.getId()))
            .toList();

        // SQSMessageResult 리스트 생성
        List<SQSMessageResult> sqsMessageResultList = memberList.stream()
            .map(alreadyRoomMember -> sqsMessageCreator.create(requester,
                alreadyRoomMember, room, NotificationType.ROOM_IN))
            .toList();

        // NotificationLog 저장
        sqsMessageResultList
            .forEach(smr -> notificationLogRepositoryService.createNotificationLog(
                smr.notificationLog()));

        // sqs 전송
        sqsMessageResultList
            .stream()
            .filter(smr -> !smr.fcmSQSMessageList().isEmpty())
            .forEach(smr -> sqsMessageSender.sendMessage(smr.fcmSQSMessageList()));
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(RejectedJoinEvent event) {
        Member manager = event.manager();
        Member requester = event.requester();
        Room room = event.room();

        //SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.createWithRoomId(manager,
            requester, room, NotificationType.REJECT_ROOM_JOIN);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }
    }

    @Async
    @TransactionalEventListener
    public void sendNotification(SentMessageEvent event) {
        Member sender = event.sender();
        Member recipient = event.recipient();
        MessageRoom messageRoom = event.messageRoom();

        //SQSMessageResult 생성
        SQSMessageResult sqsMessageResult = sqsMessageCreator.createWithMessageRoomId(sender,
            recipient, event.content(), messageRoom, NotificationType.ARRIVE_MESSAGE);

        // 알림 저장
        notificationLogRepositoryService.createNotificationLog(sqsMessageResult.notificationLog());

        // sqs 전송
        if (!sqsMessageResult.fcmSQSMessageList().isEmpty()) {
            sqsMessageSender.sendMessage(sqsMessageResult.fcmSQSMessageList());
        }
    }

    @Async
    @EventListener
    public void sendNotification(SentChatEvent sentChatEvent) {
        // 해당 채팅방에 소켓 연결되어 있는 사용자 clientId 조회 in redis
        Set<String> subscribingMembers = webSocketSessionRepository.getSubscribingMembersInChatRoom(
            String.valueOf(sentChatEvent.chatRoomId()));

        // 채팅방의 알림 수신 허용 사용자 전체 조회
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findFetchMemberByChatRoomId(
            sentChatEvent.chatRoomId());

        // 채팅방에 소켓 연결 안되어 있고, 알림 수신 허용인 사용자 추출
        List<Member> notSubscribing = chatRoomMembers.stream()
            .map(ChatRoomMember::getMember)
            .filter(m -> !subscribingMembers.contains(m.getClientId()))
            .toList();

        Member sender = memberRepositoryService.getMemberByIdOrThrow(sentChatEvent.memberId());

        List<SQSMessageResult> results = sqsMessageCreator.createWithChatRoomId(
            sender, notSubscribing, sentChatEvent.content(), sentChatEvent.chatRoomId(),
            NotificationType.ARRIVE_CHAT);

        List<FcmSQSMessage> fcmSqsMessageList = results.stream()
            .flatMap(r -> r.fcmSQSMessageList().stream())
            .toList();

        /**
         * 테스트 결과, 닉네임 최대 8글자, 채팅 content 300자 기준 dto 1개당 1212.25bytes
         * SQS 최대 메시지 크기 = 256 KiB = 262,144 bytes
         * 약 리스트 사이즈 216까지 가능
         * firebase sdk의 send_each 최대 500개까지 가능하고 SQS 메시지 하나 당 크기 고려해서 BATCH_SIZE 200으로 설정
         */
        if (!fcmSqsMessageList.isEmpty()) {
            if (fcmSqsMessageList.size() > BATCH_SIZE) {
                for (int start = 0; start < fcmSqsMessageList.size(); start += BATCH_SIZE) {
                    int end = Math.min(start + BATCH_SIZE, fcmSqsMessageList.size());

                    sqsMessageSender.sendMessage(fcmSqsMessageList.subList(start, end));
                }
            } else {
                sqsMessageSender.sendMessage(fcmSqsMessageList);
            }
        }
    }
}
