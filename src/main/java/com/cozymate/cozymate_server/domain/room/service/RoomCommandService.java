package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupRoomNameWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.HostAndMemberAndRoomTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseDto;
import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.converter.FeedConverter;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.converter.MateConverter;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.RoomUpdateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagCommandService;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomCommandService {

    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 8;

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    private final RuleRepository ruleRepository;
    private final RoomLogRepository roomLogRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImageRepository postImageRepository;
    private final RoleRepository roleRepository;
    private final FeedRepository feedRepository;
    private final RoomQueryService roomQueryService;
    private final RoomLogCommandService roomLogCommandService;
    private final ApplicationEventPublisher eventPublisher;
    private final RoomHashtagCommandService roomHashtagCommandService;


    public RoomDetailResponseDTO createPrivateRoom(PrivateRoomCreateRequestDTO request,
        Member member) {
        Member creator = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (roomRepository.existsByMemberIdAndStatuses(creator.getId(), RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPrivateRoom(request, inviteCode);
        room.enableRoom();
        room = roomRepository.save(room);
        roomLogCommandService.addRoomLogCreationRoom(room);

        Mate mate = MateConverter.toEntity(room, creator, true);
        mateRepository.save(mate);

        Feed feed = FeedConverter.toEntity(room);
        feedRepository.save(feed);

        return roomQueryService.getRoomById(room.getId(), member.getId());
    }

    public RoomDetailResponseDTO createPublicRoom(PublicRoomCreateRequestDTO request,
        Member member) {
        Member creator = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (roomRepository.existsByMemberIdAndStatuses(creator.getId(), RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPublicRoom(request, inviteCode);

        // 해시태그 저장 과정
        roomHashtagCommandService.createRoomHashtag(room, request.hashtagList());
        room = roomRepository.save(room);
        roomLogCommandService.addRoomLogCreationRoom(room);

        Mate mate = MateConverter.toEntity(room, creator, true);
        mateRepository.save(mate);

        Feed feed = FeedConverter.toEntity(room);
        feedRepository.save(feed);

        return roomQueryService.getRoomById(room.getId(), member.getId());
    }

    public void joinRoom(Long roomId, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Optional<Mate> existingMate = mateRepository.findByRoomIdAndMemberId(roomId, memberId);
        checkEntryStatus(existingMate);

        if (roomRepository.existsByMemberIdAndStatuses(memberId, RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        if (mateRepository.countActiveMatesByRoomId(roomId) >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (existingMate.isPresent()) {
            // 재입장 처리
            Mate exitingMate = existingMate.get();
            exitingMate.setEntryStatus(EntryStatus.JOINED);
            mateRepository.save(exitingMate);
            room.arrive();
            room.isRoomFull();
            roomRepository.save(room);
        } else {
            Mate mate = MateConverter.toEntity(room, member, false);
            mateRepository.save(mate);
            room.arrive();
            room.isRoomFull();
            roomRepository.save(room);
        }

        // 푸시 알림 코드
        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        eventPublisher.publishEvent(GroupRoomNameWithOutMeTargetDto.create(member, memberList, room,
            NotificationType.ROOM_IN));

    }

    public void deleteRoom(Long roomId, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate member = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        if (!member.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        // 연관된 Mate, Rule, RoomLog, Feed 엔티티 삭제
        deleteRoomDatas(roomId);
        roomRepository.delete(room);
    }

    public Boolean checkRoomName(String roomName) {
        return roomQueryService.isValidRoomName(roomName);
    }

    public void quitRoom(Long roomId, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate quittingMate = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 이미 나간 방에 대한 예외 처리
        if (quittingMate.getEntryStatus() == EntryStatus.EXITED) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MATE);
        }

        quittingMate.quit();
        mateRepository.save(quittingMate);
        room.quit();
        roomRepository.save(room);

        // 방장일 경우 가장 먼저 들어온 룸메이트에게 방장 위임
        if (quittingMate.isRoomManager()) {
            assignNewRoomManager(roomId, quittingMate);
        }

        // 방이 비었다면 방 삭제
        if (room.getNumOfArrival() == 0) {
            // 연관된 Mate, Rule, RoomLog, Feed 엔티티 삭제
            deleteRoomDatas(roomId);
            roomRepository.delete(room);
            return;
        }

        // 푸시 알림 코드
        List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room, EntryStatus.JOINED);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        eventPublisher.publishEvent(GroupRoomNameWithOutMeTargetDto.create(member, memberList, room,
            NotificationType.ROOM_OUT));
    }

    private void assignNewRoomManager(Long roomId, Mate quittingMate) {
        quittingMate.setNotRoomManager();
        mateRepository.save(quittingMate);
        List<Mate> remainingMates = mateRepository.findAllByRoomIdAndEntryStatus(roomId, EntryStatus.JOINED);

        if (remainingMates.isEmpty()) {
            return;
        }
        Mate newManager = remainingMates.stream()
            .min(Comparator.comparing(Mate::getCreatedAt))
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND)); // 방장 없음 예외 처리
        newManager.setRoomManager();
    }

    private void deleteRoomDatas(Long roomId) {
        List<Mate> mates = mateRepository.findByRoomId(roomId);
        for (Mate mate : mates) {
            roleRepository.deleteByMateId(mate.getId());
            todoRepository.deleteByMateId(mate.getId());
        }
        mateRepository.deleteByRoomId(roomId);
        ruleRepository.deleteByRoomId(roomId);
        roomLogRepository.deleteByRoomId(roomId);

        // 피드 삭제 로직
        if (feedRepository.existsByRoomId(roomId)) {
            Feed feed = feedRepository.findByRoomId(roomId);
            List<Post> posts = postRepository.findByFeedId(feed.getId());
            for (Post post : posts) {
                postCommentRepository.deleteAllByPostId(post.getId());
                postImageRepository.deleteAllByPostId(post.getId());
            }
            postRepository.deleteByFeedId(feed.getId());
            feedRepository.deleteByRoomId(roomId);
        }
    }

    public RoomDetailResponseDTO updateRoom(Long roomId, Long memberId,
        RoomUpdateRequestDTO request) {

        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate member = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        if (!member.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() == RoomType.PUBLIC) {
            roomHashtagCommandService.deleteRoomHashtags(room);
            roomHashtagCommandService.updateRoomHashtags(room, request.hashtagList());
        }
        room.updateRoom(request.name(), request.persona());
        roomRepository.save(room);

        return roomQueryService.getRoomById(roomId, memberId);
    }

    public void sendInvitation(Long inviteeId, Long inviterId) {
        Member inviteeMember = memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 방장이 속한 방의 정보
        Room room = roomRepository.findById(roomQueryService.getExistRoom(inviterId).roomId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 초대한 사용자가 방장인지 검증
        Mate inviter = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!inviter.getMember().getId().equals(inviterId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        // 이미 참가한 방인지 검사
        Optional<Mate> invitee = mateRepository.findByRoomIdAndMemberId(room.getId(), inviteeId);
        checkEntryStatus(invitee);

        // 초대하려는 사용자가 속한 방이 있는지 검사
        if (roomRepository.existsByMemberIdAndStatuses(inviteeId, RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        // 방 정원 검사
        if (mateRepository.countActiveMatesByRoomId(room.getId()) >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (invitee.isPresent()) {
            Mate mate = invitee.get();
            mate.setEntryStatus(EntryStatus.INVITED);
            mateRepository.save(mate);
        } else {
            Mate mate = MateConverter.toInvitation(room, inviteeMember, false);
            mateRepository.save(mate);
        }

        // 푸시 알림 코드
        eventPublisher.publishEvent(
            HostAndMemberAndRoomTargetDto.create(inviter.getMember(), NotificationType.SEND_ROOM_INVITE,
                inviteeMember, NotificationType.ARRIVE_ROOM_INVITE, room));
    }

    public void respondToInvitation(Long roomId, Long inviteeId, boolean accept) {
        Member inviteeMember = memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 초대 상태가 아니면 예외
        Mate invitee = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, inviteeId,
                EntryStatus.INVITED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));

        // 만약 WAITING 또는 ENABLE 상태의 방에 이미 참여했다면 예외 발생
        if (mateRepository.existsByMemberIdAndEntryStatusAndRoomStatusIn(
            inviteeId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING))) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        if (room.getNumOfArrival() >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (accept) {
            // 초대 요청을 수락하여 JOINED 상태로 변경
            invitee.setEntryStatus(EntryStatus.JOINED);
            mateRepository.save(invitee);
            room.arrive();
            room.isRoomFull();
        } else {
            // 초대 요청을 거절하여 PENDING 상태를 삭제
            mateRepository.delete(invitee);
        }
        roomRepository.save(room);

        // 푸시 알림 코드
        Mate inviterMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true).orElseThrow(
            () -> new GeneralException(ErrorStatus._MATE_NOT_FOUND)
        );
        Member inviterMember = inviterMate.getMember();

        if (accept) {
            eventPublisher.publishEvent(OneTargetReverseDto.create(inviteeMember, inviterMember,
                NotificationType.ACCEPT_ROOM_INVITE));

            List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room,
                EntryStatus.JOINED);

            List<Member> memberList = findRoomMates.stream()
                .map(Mate::getMember)
                .filter(findMember -> !findMember.getId().equals(inviteeMember.getId()))
                .toList();

            eventPublisher.publishEvent(
                GroupRoomNameWithOutMeTargetDto.create(inviteeMember, memberList, room,
                    NotificationType.ROOM_IN));
        } else {
            eventPublisher.publishEvent(OneTargetReverseDto.create(inviteeMember, inviterMember,
                NotificationType.REJECT_ROOM_INVITE));
        }
    }

    public void forceQuitRoom(Long roomId, Long targetMemberId, Long managerId) {
        // 방장이 본인을 퇴장시킬 수 없음
        if (managerId.equals(targetMemberId)) {
            throw new GeneralException(ErrorStatus._CANNOT_SELF_FORCED_QUIT);
        }
        memberRepository.findById(managerId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate managerMate = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, managerId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 방장이 아니면 예외 발생
        if (!managerMate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }
        quitRoom(roomId, targetMemberId);
    }

    public void cancelInvitation(Long inviteeId, Long inviterId) {

        memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomQueryService.getExistRoom(inviterId).roomId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 초대한 사용자가 방장인지 검증
        Mate inviter = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!inviter.getMember().getId().equals(inviterId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        Mate invitee = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), inviteeId,
                EntryStatus.INVITED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));

        mateRepository.delete(invitee);

    }

    public void requestToJoin(Long roomId, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (roomRepository.existsByMemberIdAndStatuses(memberId, RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        Optional<Mate> existingMate = mateRepository.findByRoomIdAndMemberId(room.getId(), memberId);
        checkEntryStatus(existingMate);

        if (room.getNumOfArrival() >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (existingMate.isPresent()) {
            Mate mate = existingMate.get();
            mate.setEntryStatus(EntryStatus.PENDING);
            mateRepository.save(mate);
        } else {
            Mate mate = MateConverter.toPending(room, member, false);
            mateRepository.save(mate);
        }

        // 푸시 알림 코드
        Mate managerMate = mateRepository.findFetchByRoomAndIsRoomManager(room, true).orElseThrow(
            () -> new GeneralException(ErrorStatus._MATE_NOT_FOUND)
        );

        Member managerMember = managerMate.getMember();

        eventPublisher.publishEvent(OneTargetReverseDto.create(member, managerMember,
            NotificationType.ARRIVE_ROOM_JOIN_REQUEST));

    }

    private void checkEntryStatus(Optional<Mate> existingMate) {
        if (existingMate.isPresent()) {
            EntryStatus status = existingMate.get().getEntryStatus();
            switch (status) {
                case JOINED:
                    throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
                case PENDING:
                    throw new GeneralException(ErrorStatus._REQUEST_ALREADY_SENT);
                case INVITED:
                    throw new GeneralException(ErrorStatus._INVITATION_ALREADY_SENT);
                default:
                    break;
            }
        }
    }

    public void cancelRequestToJoin(Long roomId, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate mate = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                EntryStatus.PENDING)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REQUEST_NOT_FOUND));

        mateRepository.delete(mate);
    }

    public void respondToJoinRequest(Long requesterId, boolean accept, Long managerId) {
        Member requestMember = memberRepository.findById(requesterId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 방장이 속한 방의 정보
        Room room = roomRepository.findById(roomQueryService.getExistRoom(managerId).roomId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 방장인지 검증
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        // 만약 WAITING 또는 ENABLE 상태의 방에 이미 참여했다면 예외 발생
        if (mateRepository.existsByMemberIdAndEntryStatusAndRoomStatusIn(
            requesterId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING))) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        Mate requester = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(),
                requesterId, EntryStatus.PENDING)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REQUEST_NOT_FOUND));

        if (room.getNumOfArrival() + 1 > room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (accept) {
            requester.setEntryStatus(EntryStatus.JOINED);
            mateRepository.save(requester);
            room.arrive();
            room.isRoomFull();
        } else {
            mateRepository.delete(requester); // 거절 시 요청자 삭제
        }
        roomRepository.save(room);

        // 푸시 알림 코드
        if (accept) {
            eventPublisher.publishEvent(HostAndMemberAndRoomTargetDto.create(manager.getMember(),
                NotificationType.SELF_ACCEPT_ROOM_JOIN, requestMember,
                NotificationType.ACCEPT_ROOM_JOIN, room));

            List<Mate> findRoomMates = mateRepository.findFetchMemberByRoom(room,
                EntryStatus.JOINED);

            List<Member> memberList = findRoomMates.stream()
                .map(Mate::getMember)
                .filter(findMember -> !findMember.getId().equals(requestMember.getId()))
                .toList();

            eventPublisher.publishEvent(
                GroupRoomNameWithOutMeTargetDto.create(requestMember, memberList, room,
                    NotificationType.ROOM_IN));
        } else {
            eventPublisher.publishEvent(HostAndMemberAndRoomTargetDto.create(manager.getMember(),
                NotificationType.SELF_REJECT_ROOM_JOIN, requestMember,
                NotificationType.REJECT_ROOM_JOIN, room));
        }
    }

    public void changeToPublicRoom(Long roomId, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate member = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 방장이 아니면 예외 발생
        if (!member.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() != RoomType.PRIVATE) {
            throw new GeneralException(ErrorStatus._PUBLIC_ROOM);
        }

        room.changeToPublicRoom();
    }

    public void changeToPrivateRoom(Long roomId, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate member = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        if (!member.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() != RoomType.PUBLIC) {
            throw new GeneralException(ErrorStatus._PRIVATE_ROOM);
        }

        room.changeToPrivateRoom();
    }

    // 초대코드 생성 부분
    private String generateUniqueUppercaseKey() {
        String randomKey;
        do {
            randomKey = generateUppercaseKey();
        } while (!isKeyUnique(randomKey));
        return randomKey;
    }

    private boolean isKeyUnique(String key) {
        return !roomRepository.existsByInviteCode(key);
    }

    private String generateUppercaseKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            int index = random.nextInt(UPPERCASE_ALPHABET.length());
            key.append(UPPERCASE_ALPHABET.charAt(index));
        }
        return key.toString();
    }
}
