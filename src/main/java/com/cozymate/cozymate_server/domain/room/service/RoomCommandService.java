package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.fcm.event.converter.EventConverter;
import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.converter.FeedConverter;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.converter.MateConverter;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.role.service.RoleCommandService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.RoomUpdateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.room.validator.RoomValidator;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagCommandService;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.domain.university.University;
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
    private final RoleCommandService roleCommandService;
    private final TodoCommandService todoCommandService;
    private final RoomFavoriteRepository roomFavoriteRepository;
    private final RoomHashtagRepository roomHashtagRepository;
    private final RoomValidator roomValidator;
    private final RoomRepositoryService roomRepositoryService;

    @Transactional
    public RoomDetailResponseDTO createPrivateRoom(PrivateRoomCreateRequestDTO request,
        Member member) {
        roomValidator.isAlreadyJoinedRoom(member.getId());

        // 기존 참여 요청들 삭제
        clearOtherRoomRequests(member.getId());

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPrivateRoom(request, inviteCode);
        room.enableRoom();
        room = roomRepositoryService.save(room);
        roomLogCommandService.addRoomLogCreationRoom(room);

        Mate mate = MateConverter.toEntity(room, member, true);
        mateRepository.save(mate);

        Feed feed = FeedConverter.toEntity(room);
        feedRepository.save(feed);

        return roomQueryService.getRoomById(room.getId(), member.getId());
    }

    @Transactional
    public RoomDetailResponseDTO createPublicRoom(PublicRoomCreateRequestDTO request,
        Member member) {

        roomValidator.isAlreadyJoinedRoom(member.getId());

        // memberStat이 null일 경우 공개방 생성 불가
        if (member.getMemberStat() == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS);
        }

        // 기존 참여 요청들 삭제
        clearOtherRoomRequests(member.getId());

        Gender gender = member.getGender();
        University university = member.getUniversity();

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPublicRoom(request, inviteCode, gender, university);

        // 해시태그 저장 과정
        roomHashtagCommandService.createRoomHashtag(room, request.hashtagList());
        room = roomRepositoryService.save(room);
        roomLogCommandService.addRoomLogCreationRoom(room);

        Mate mate = MateConverter.toEntity(room, member, true);
        mateRepository.save(mate);

        Feed feed = FeedConverter.toEntity(room);
        feedRepository.save(feed);

        return roomQueryService.getRoomById(room.getId(), member.getId());
    }

    @Transactional
    public void joinRoom(Long roomId, Member member) {

        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        Optional<Mate> existingMate = mateRepository.findByRoomIdAndMemberId(roomId, member.getId());
        roomValidator.checkEntryStatus(existingMate);

        roomValidator.isAlreadyJoinedRoom(member.getId());
        roomValidator.isRoomFull(room);

        if (existingMate.isPresent()) {
            // 재입장 처리
            processJoinRequest(existingMate.get(), room);
            clearOtherRoomRequests(member.getId());
        } else {
            Mate mate = MateConverter.toEntity(room, member, false);
            mateRepository.save(mate);
            room.arrive();
            room.isRoomFull();
        }
        roomRepositoryService.save(room);

        eventPublisher.publishEvent(EventConverter.toJoinedRoomEvent(member, room));
    }

    @Transactional
    public void deleteRoom(Long roomId, Long memberId) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);
        roomValidator.checkRoomMember(roomId, memberId);
        roomValidator.checkRoomManager(roomId, memberId);

        // 연관된 Mate, Rule, RoomLog, Feed 엔티티 삭제
        deleteRoomDatas(roomId);
        roomRepositoryService.delete(room);
    }

    public Boolean checkRoomName(String roomName) {
        return roomValidator.isValidRoomName(roomName);
    }

    @Transactional
    public void quitRoom(Long roomId, Long memberId) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);
        Mate quittingMate = roomValidator.checkRoomMember(roomId, memberId);

        // 이미 나간 방에 대한 예외 처리
        if (quittingMate.getEntryStatus() == EntryStatus.EXITED) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MATE);
        }

        // 방을 나갈 때 Role과 투두 삭제
        todoCommandService.updateAssignedMateIfMateExitRoom(quittingMate);
        roleCommandService.updateAssignedMateIfMateExitRoom(quittingMate, roomId);

        quittingMate.quit();
        mateRepository.save(quittingMate);
        room.quit();
        roomRepositoryService.save(room);

        // 방장일 경우 가장 먼저 들어온 룸메이트에게 방장 위임
        if (quittingMate.isRoomManager()) {
            assignNewRoomManager(roomId, quittingMate);
        }

        // 방이 비었다면 방 삭제
        if (room.getNumOfArrival() == 0) {
            // 연관된 Mate, Rule, RoomLog, Feed 엔티티 삭제
            deleteRoomDatas(roomId);
            roomRepositoryService.delete(room);
            return;
        }

        eventPublisher.publishEvent(EventConverter.toQuitRoomEvent(quittingMate.getMember(), room));
    }

    private void assignNewRoomManager(Long roomId, Mate quittingMate) {
        quittingMate.setNotRoomManager();
        mateRepository.save(quittingMate);
        List<Mate> remainingMates = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);

        if (remainingMates.isEmpty()) {
            return;
        }
        Mate newManager = remainingMates.stream()
            .min(Comparator.comparing(Mate::getCreatedAt))
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND)); // 방장 없음 예외 처리
        newManager.setRoomManager();
    }

    private void deleteRoomDatas(Long roomId) {
        roomLogRepository.deleteAllByRoomId(roomId);
        todoRepository.deleteAllByRoomId(roomId);
        roleRepository.deleteAllByRoomId(roomId);
        mateRepository.deleteAllByRoomId(roomId);
        ruleRepository.deleteAllByRoomId(roomId);
        roomFavoriteRepository.deleteAllByRoomId(roomId);
        roomHashtagRepository.deleteAllByRoomId(roomId);

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

    @Transactional
    public RoomDetailResponseDTO updateRoom(Long roomId, Long memberId,
        RoomUpdateRequestDTO request) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        Mate mate = roomValidator.checkRoomMember(roomId, memberId);

        if (!mate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() == RoomType.PUBLIC) {
            roomHashtagCommandService.deleteRoomHashtags(room);
            roomHashtagCommandService.createRoomHashtag(room, request.hashtagList());
        }
        room.updateRoom(request.name(), request.persona());
        roomRepositoryService.save(room);

        return roomQueryService.getRoomById(roomId, memberId);
    }

    @Transactional
    public void sendInvitation(Long inviteeId, Member inviteeMember) {
        memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        // 방장이 속한 방의 정보
        Room room = roomRepositoryService.getRoomOrThrow(roomQueryService.getExistRoom(inviteeMember.getId()).roomId());

        // 초대한 사용자가 방장인지 검증
        Mate inviter = roomValidator.checkRoomManager(room.getId(), inviteeMember.getId());

        // 이미 참가한 방인지 검사
        Optional<Mate> invitee = mateRepository.findByRoomIdAndMemberId(room.getId(), inviteeId);
        roomValidator.checkEntryStatus(invitee);

        // 초대하려는 사용자가 속한 방이 있는지 검사
        roomValidator.isAlreadyJoinedRoom(inviteeId);

        // 방 정원 검사
        roomValidator.isRoomFull(room);

        if (invitee.isPresent()) {
            Mate mate = invitee.get();
            mate.setEntryStatus(EntryStatus.INVITED);
            mateRepository.save(mate);
        } else {
            Mate mate = MateConverter.toInvitation(room, inviteeMember, false);
            mateRepository.save(mate);
        }

        eventPublisher.publishEvent(
            EventConverter.toSentInvitationEvent(inviter.getMember(), inviteeMember, room));
    }

    @Transactional
    public void respondToInvitation(Long roomId, Member inviteeMember, boolean accept) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        // 초대 상태가 아니면 예외
        Mate invitee = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId,
                inviteeMember.getId(), EntryStatus.INVITED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));

        // 만약 WAITING 또는 ENABLE 상태의 방에 이미 참여했다면 예외 발생
        roomValidator.isAlreadyJoinedRoom(inviteeMember.getId());

        roomValidator.isRoomFull(room);

        if (accept) {
            // 초대 요청을 수락하여 JOINED 상태로 변경
            processJoinRequest(invitee, room);
            clearOtherRoomRequests(inviteeMember.getId());

            eventPublisher.publishEvent(
                EventConverter.toAcceptedInvitationEvent(inviteeMember, room));
        } else {
            // 초대 요청을 거절하여 PENDING 상태를 삭제
            mateRepository.delete(invitee);

            eventPublisher.publishEvent(
                EventConverter.toRejectedInvitationEvent(inviteeMember, room));
        }
    }

    public void forceQuitRoom(Long roomId, Long targetMemberId, Member manager) {
        memberRepository.findById(targetMemberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 방장이 본인을 퇴장시킬 수 없음
        if (manager.getId().equals(targetMemberId)) {
            throw new GeneralException(ErrorStatus._CANNOT_SELF_FORCED_QUIT);
        }

        roomValidator.checkRoomManager(roomId, manager.getId());

        quitRoom(roomId, targetMemberId);
    }

    @Transactional
    public void cancelInvitation(Long inviteeId, Member inviter) {
        memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepositoryService.getRoomOrThrow(roomQueryService.getExistRoom(inviter.getId()).roomId());

        // 초대한 사용자가 방장인지 검증
        roomValidator.checkRoomManager(room.getId(), inviter.getId());

        Mate invitee = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), inviteeId,
                EntryStatus.INVITED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));

        mateRepository.delete(invitee);
    }

    @Transactional
    public void requestToJoin(Long roomId, Member member) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        roomValidator.isAlreadyJoinedRoom(member.getId());

        Optional<Mate> existingMate = mateRepository.findByRoomIdAndMemberId(room.getId(),
            member.getId());
        roomValidator.checkEntryStatus(existingMate);

        roomValidator.isRoomFull(room);

        if (existingMate.isPresent()) {
            Mate mate = existingMate.get();
            mate.setEntryStatus(EntryStatus.PENDING);
            mateRepository.save(mate);
        } else {
            Mate mate = MateConverter.toPending(room, member, false);
            mateRepository.save(mate);
        }

        eventPublisher.publishEvent(EventConverter.toRequestedJoinRoomEvent(member, room));
    }

    @Transactional
    public void cancelRequestToJoin(Long roomId, Member member) {
        roomRepositoryService.getRoomOrThrow(roomId);

        Mate mate = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, member.getId(),
                EntryStatus.PENDING)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REQUEST_NOT_FOUND));

        mateRepository.delete(mate);
    }

    @Transactional
    public void respondToJoinRequest(Long requesterId, boolean accept, Long managerId) {
        Member requestMember = memberRepository.findById(requesterId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 방장이 속한 방의 정보
        Room room = roomRepositoryService.getRoomOrThrow(roomQueryService.getExistRoom(managerId).roomId());

        // 방장인지 검증
        Mate manager = roomValidator.checkRoomManager(room.getId(), managerId);

        // 만약 WAITING 또는 ENABLE 상태의 방에 이미 참여했다면 예외 발생
        roomValidator.isAlreadyJoinedRoom(requesterId);

        Mate requester = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(),
                requesterId, EntryStatus.PENDING)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REQUEST_NOT_FOUND));

        roomValidator.isRoomFull(room);

        if (accept) {
            processJoinRequest(requester, room);
            clearOtherRoomRequests(requesterId);

            eventPublisher.publishEvent(
                EventConverter.toAcceptedJoinEvent(manager.getMember(), requestMember, room));
        } else {
            mateRepository.delete(requester); // 거절 시 요청자 삭제

            eventPublisher.publishEvent(
                EventConverter.toRejectedJoinEvent(manager.getMember(), requestMember));
        }
    }

    @Transactional
    public void changeToPublicRoom(Long roomId, Member manager) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        Mate managerMate = roomValidator.checkRoomMember(roomId, manager.getId());

        // 방장이 아니면 예외 발생
        if (!managerMate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() != RoomType.PRIVATE) {
            throw new GeneralException(ErrorStatus._PUBLIC_ROOM);
        }

        List<Mate> mates = mateRepository.findFetchMemberByRoomAndEntryStatus(room,
            EntryStatus.JOINED);

        Gender roomGender = manager.getGender();
        University roomUniversity = manager.getUniversity();

        for (Mate mate : mates) {
            Member member = mate.getMember();
            if (!member.getGender().equals(roomGender)) {
                throw new GeneralException(ErrorStatus._MISMATCH_GENDER);
            }
            if (!member.getUniversity().equals(roomUniversity)) {
                throw new GeneralException(ErrorStatus._MISMATCH_UNIVERSITY);
            }
        }

        room.changeToPublicRoom(roomGender, roomUniversity);
    }

    @Transactional
    public void changeToPrivateRoom(Long roomId, Long memberId) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);

        Mate mate = roomValidator.checkRoomMember(roomId, memberId);

        if (!mate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType() != RoomType.PUBLIC) {
            throw new GeneralException(ErrorStatus._PRIVATE_ROOM);
        }

        room.changeToPrivateRoom();
    }

    private void processJoinRequest(Mate mate, Room room) {
        mate.setEntryStatus(EntryStatus.JOINED);
        mateRepository.save(mate);
        room.arrive();
        room.isRoomFull();
    }

    private void clearOtherRoomRequests(Long memberId) {
        mateRepository.deleteAllByMemberIdAndEntryStatusIn(
            memberId, List.of(EntryStatus.PENDING, EntryStatus.INVITED)
        );
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
        return !roomRepositoryService.existsRoomByInviteCode(key);
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
