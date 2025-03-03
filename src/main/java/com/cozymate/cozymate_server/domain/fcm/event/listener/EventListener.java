package com.cozymate.cozymate_server.domain.fcm.event.listener;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupRoomNameWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.HostAndMemberAndRoomTargetDto;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseDto;
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
        Member member = event.getMember();
        Room room = event.getRoom();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDto groupRoomNameWithOutMeTargetDto = GroupRoomNameWithOutMeTargetDto.create(
            member, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(QuitRoomEvent event) {
        Member member = event.getMember();
        Room room = event.getRoom();

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDto groupRoomNameWithOutMeTargetDto = GroupRoomNameWithOutMeTargetDto.create(
            member, memberList, room, NotificationType.ROOM_OUT);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(SentInvitationEvent event) {
        Member inviter = event.getInviter();
        Member invitee = event.getInvitee();
        Room room = event.getRoom();

        HostAndMemberAndRoomTargetDto hostAndMemberAndRoomTargetDto = HostAndMemberAndRoomTargetDto.create(
            inviter, NotificationType.SEND_ROOM_INVITE, invitee,
            NotificationType.ARRIVE_ROOM_INVITE, room);

        fcmPushService.sendNotification(hostAndMemberAndRoomTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(AcceptedInvitationEvent event) {
        Member invitee = event.getInvitee();
        Room room = event.getRoom();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(invitee, inviter,
            NotificationType.ACCEPT_ROOM_INVITE);

        fcmPushService.sendNotification(oneTargetReverseDto);

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(invitee.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDto groupRoomNameWithOutMeTargetDto = GroupRoomNameWithOutMeTargetDto.create(
            invitee, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(RejectedInvitationEvent event) {
        Member invitee = event.getInvitee();
        Room room = event.getRoom();

        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        Member inviter = inviterMate.getMember();

        OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(invitee, inviter,
            NotificationType.REJECT_ROOM_INVITE);

        fcmPushService.sendNotification(oneTargetReverseDto);
    }

    @TransactionalEventListener
    public void sendNotification(RequestedJoinRoomEvent event) {
        Member member = event.getMember();
        Room room = event.getRoom();

        Mate managerMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        Member managerMember = managerMate.getMember();

        OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(member, managerMember,
            NotificationType.ARRIVE_ROOM_JOIN_REQUEST);

        fcmPushService.sendNotification(oneTargetReverseDto);
    }

    @TransactionalEventListener
    public void sendNotification(AcceptedJoinEvent event) {
        Member manager = event.getManager();
        Member requester = event.getRequester();
        Room room = event.getRoom();

        OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(manager, requester,
            NotificationType.ACCEPT_ROOM_JOIN);

        fcmPushService.sendNotification(oneTargetReverseDto);

        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(requester.getId()))
            .toList();

        GroupRoomNameWithOutMeTargetDto groupRoomNameWithOutMeTargetDto = GroupRoomNameWithOutMeTargetDto.create(
            requester, memberList, room, NotificationType.ROOM_IN);

        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(RejectedJoinEvent event) {
        Member manager = event.getManager();
        Member requester = event.getRequester();

        OneTargetReverseDto oneTargetReverseDto = OneTargetReverseDto.create(manager, requester,
            NotificationType.REJECT_ROOM_JOIN);

        fcmPushService.sendNotification(oneTargetReverseDto);
    }
}