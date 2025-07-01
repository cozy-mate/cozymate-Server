package com.cozymate.cozymate_server.domain.fcm.event.listener;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.GroupRoomNameWithOutMeTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.HostAndMemberAndRoomTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.dto.push.target.OneTargetReverseDTO;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentChatEvent;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.JoinedRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.QuitRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RequestedJoinRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentInvitationEvent;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EventListener {

    private final FcmPushService fcmPushService;
    private final MateRepository mateRepository;

    @TransactionalEventListener
    public void sendNotification(JoinedRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDTO groupRoomNameWithOutMeTargetDTO = GroupRoomNameWithOutMeTargetDTO.create(
            member, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDTO);
    }

    @TransactionalEventListener
    public void sendNotification(QuitRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDTO groupRoomNameWithOutMeTargetDTO = GroupRoomNameWithOutMeTargetDTO.create(
            member, memberList, room, NotificationType.ROOM_OUT);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDTO);
    }

    @TransactionalEventListener
    public void sendNotification(SentInvitationEvent event) {
        Member inviter = event.inviter();
        Member invitee = event.invitee();
        Room room = event.room();

        HostAndMemberAndRoomTargetDTO hostAndMemberAndRoomTargetDTO = HostAndMemberAndRoomTargetDTO.create(
            inviter, NotificationType.SEND_ROOM_INVITE, invitee,
            NotificationType.ARRIVE_ROOM_INVITE, room);

        fcmPushService.sendNotification(hostAndMemberAndRoomTargetDTO);
    }

    @TransactionalEventListener
    public void sendNotification(AcceptedInvitationEvent event) {
        Member invitee = event.invitee();
        Room room = event.room();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(invitee, inviter,
            NotificationType.ACCEPT_ROOM_INVITE);

        fcmPushService.sendNotification(oneTargetReverseDTO);

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(invitee.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDTO groupRoomNameWithOutMeTargetDTO = GroupRoomNameWithOutMeTargetDTO.create(
            invitee, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDTO);
    }

    @TransactionalEventListener
    public void sendNotification(RejectedInvitationEvent event) {
        Member invitee = event.invitee();
        Room room = event.room();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(invitee, inviter,
            NotificationType.REJECT_ROOM_INVITE);

        fcmPushService.sendNotification(oneTargetReverseDTO);
    }

    @TransactionalEventListener
    public void sendNotification(RequestedJoinRoomEvent event) {
        Member member = event.member();
        Room room = event.room();

        Mate managerMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        Member managerMember = managerMate.getMember();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(member, managerMember,
            NotificationType.ARRIVE_ROOM_JOIN_REQUEST, room);

        fcmPushService.sendNotification(oneTargetReverseDTO);
    }

    @TransactionalEventListener
    public void sendNotification(AcceptedJoinEvent event) {
        Member manager = event.manager();
        Member requester = event.requester();
        Room room = event.room();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(manager, requester,
            NotificationType.ACCEPT_ROOM_JOIN);

        fcmPushService.sendNotification(oneTargetReverseDTO);

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(requester.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDTO groupRoomNameWithOutMeTargetDTO = GroupRoomNameWithOutMeTargetDTO.create(
            requester, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDTO);
    }

    @TransactionalEventListener
    public void sendNotification(RejectedJoinEvent event) {
        Member manager = event.manager();
        Member requester = event.requester();
        Room room = event.room();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(manager, requester,
            NotificationType.REJECT_ROOM_JOIN, room);

        fcmPushService.sendNotification(oneTargetReverseDTO);
    }

    @TransactionalEventListener
    public void sendNotification(SentChatEvent event) {
        Member sender = event.sender();
        Member recipient = event.recipient();
        ChatRoom chatRoom = event.chatRoom();

        OneTargetReverseDTO oneTargetReverseDTO = OneTargetReverseDTO.create(sender, recipient,
            NotificationType.ARRIVE_CHAT, event.content(), chatRoom);

        fcmPushService.sendNotification(oneTargetReverseDTO);
    }
}