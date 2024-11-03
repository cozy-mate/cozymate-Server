package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupRoomNameWithOutMeTargetDto;
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
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.PrivateRoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.PublicRoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.RoomUpdateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomCreateResponse;
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


    public RoomCreateResponse createPrivateRoom(PrivateRoomCreateRequest request, Member member) {
        Member creator = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (roomRepository.existsByMemberIdAndStatuses(creator.getId(), RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPrivateRoom(request, inviteCode);
        room = roomRepository.save(room);
        roomLogCommandService.addRoomLogCreationRoom(room);

        Mate mate = MateConverter.toEntity(room, creator, true);
        mateRepository.save(mate);

        Feed feed = FeedConverter.toEntity(room);
        feedRepository.save(feed);

        return roomQueryService.getRoomById(room.getId(), member.getId());
    }

    public RoomCreateResponse createPublicRoom(PublicRoomCreateRequest request, Member member) {
        Member creator = memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (roomRepository.existsByMemberIdAndStatuses(creator.getId(), RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        String inviteCode = generateUniqueUppercaseKey();
        Room room = RoomConverter.toPublicRoom(request, inviteCode);

        // 해시태그 저장 과정
        roomHashtagCommandService.createRoomHashtag(room, request.getHashtags());
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

        Optional<Mate> isExistingMate = mateRepository.findByRoomIdAndMemberId(roomId, memberId);

        if (isExistingMate.isPresent()) {
            Mate exitingMate = isExistingMate.get();
            if (exitingMate.getEntryStatus() == EntryStatus.JOINED || exitingMate.getEntryStatus() == EntryStatus.PENDING) {
                throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
            }
        }

        if (roomRepository.existsByMemberIdAndStatuses(memberId, RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }

        if (mateRepository.countActiveMatesByRoomId(roomId) >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        if (isExistingMate.isPresent()) {
            // 재입장 처리
            Mate exitingMate = isExistingMate.get();
            exitingMate.setEntryStatus(EntryStatus.JOINED);
            exitingMate.setNotExit();
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

        // Room의 Mate들을 찾아온다
        List<Mate> findRoomMates = mateRepository.findByRoom(room);

        List<Member> memberList = findRoomMates.stream()
            .map(Mate::getMember)
            .filter(findMember -> !findMember.getId().equals(member.getId()))
            .toList();

        // 알림 내용에는 현재 코드 상 member의 이름이 담겨야하고, 현재 코드 상의 room의 이름도 담긴다
        // 알림을 받는 대상은 방에 있는 메이트들이다.
        // 넘겨야 할 파라미터 = member, room, memberList(알림 받을 대상 멤버 리스트), NotificationType
        eventPublisher.publishEvent(GroupRoomNameWithOutMeTargetDto.create(member, memberList, room,
            NotificationType.JOIN_ROOM));

    }

    public void deleteRoom(Long roomId, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(memberId)) {
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
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate quittingMate = mateRepository.findByRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 이미 나간 방에 대한 예외 처리
        if (quittingMate.getEntryStatus() == EntryStatus.EXITED) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MATE);
        }

        quittingMate.quit();
        mateRepository.save(quittingMate);
        room.quit();
        roomRepository.save(room);

        if (room.getNumOfArrival()==0) {
            // 연관된 Mate, Rule, RoomLog, Feed 엔티티 삭제
            deleteRoomDatas(roomId);
            roomRepository.delete(room);
        }
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
                postCommentRepository.deleteByPostId(post.getId());
                postImageRepository.deleteByPostId(post.getId());
            }
            postRepository.deleteByFeedId(feed.getId());
            feedRepository.deleteByRoomId(roomId);
        }
    }

    public RoomCreateResponse updateRoom(Long roomId, Long memberId, RoomUpdateRequest request){

        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        if (room.getRoomType()==RoomType.PUBLIC) {
            roomHashtagCommandService.deleteRoomHashtags(room);
            roomHashtagCommandService.updateRoomHashtags(room, request.getHashtags());
        }
        room.updateRoomName(request.getName());
        roomRepository.save(room);

        return roomQueryService.getRoomById(roomId, memberId);
    }

//    public void sendInvitation(Long inviteeId, Long inviterId) {
//        memberRepository.findById(inviterId)
//            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
//
////        Room room = roomRepository.findById(roomId)
////            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
////
////        // 초대한 사용자가 방장인지 검증
////        mateRepository.findByRoomIdAndMemberId(roomId, inviterId)
////            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
////
////
////        Mate inviter = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
////            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
////        if (!inviter.getMember().getId().equals(inviterId)) {
////            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
////        }
//
//        Room room = roomRepository.findById(roomQueryService.getExistRoom(inviterId).getRoomId())
//            .orElseThrow(()-> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
//
////        Long inviterRoomId = roomQueryService.getExistRoom(inviterId).getRoomId();
////        if (inviterRoomId == 0) {
////            throw new GeneralException(ErrorStatus._NOT_IN_ANY_ROOM); // 초대자가 방에 속해 있지 않은 경우 예외 처리
////        }
////
////        Room room = roomRepository.findById(inviterRoomId)
////            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
//
//
//        // 이미 참가한 방인지 검사
//        Optional<Mate> isExistingMate = mateRepository.findByRoomIdAndMemberId(room.getId(), inviteeId);
//
//        if (isExistingMate.isPresent()) {
//            Mate exitingMate = isExistingMate.get();
//            if (exitingMate.getEntryStatus() == EntryStatus.JOINED || exitingMate.getEntryStatus() == EntryStatus.PENDING) {
//                throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
//            }
//        }
//
//        // 초대하려는 사용자가 속한 방이 있는지 검사
//        RoomExistResponse invitee_existingRoom = roomQueryService.getExistRoom(inviteeId);
//        if (invitee_existingRoom != null && invitee_existingRoom.getRoomId() != 0) {
//            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
//        }
//
//
//        // 방 정원 검사
//        if (mateRepository.countActiveMatesByRoomId(room.getId()) >= room.getMaxMateNum()) {
//            throw new GeneralException(ErrorStatus._ROOM_FULL);
//        }
//
//            Optional<Mate> invitee = mateRepository.findByRoomIdAndMemberId(room.getId(), inviteeId);
//            if (invitee.isPresent()) {
//                if (invitee.get().getEntryStatus() == EntryStatus.PENDING) {
//                    throw new GeneralException(ErrorStatus._INVITATION_ALREADY_SENT);
//                } else {
//                    throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
//                }
//            }
//
//            if (roomRepository.existsByMemberIdAndStatuses(inviteeId, RoomStatus.ENABLE,
//                RoomStatus.WAITING, EntryStatus.JOINED)) {
//                throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
//            }
//
//            Mate mate = MateConverter.toInvitation(room, member, false);
//            mateRepository.save(mate);
//        }
//
//    }

    @Deprecated
    public void respondToInviteRequest(Long roomId, Long inviteeId, boolean accept) {
        memberRepository.findById(inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (room.getNumOfArrival() + 1 > room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }

        Mate invitee = mateRepository.findByRoomIdAndMemberId(roomId, inviteeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));

        if (invitee.getEntryStatus() == EntryStatus.JOINED) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
        }

        // 만약 WAITING 또는 ENABLE 상태의 방에 이미 참여했다면 예외 발생
        if (mateRepository.existsByMemberIdAndEntryStatusAndRoomStatusIn(
            inviteeId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING))) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
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

        Mate managerMate = mateRepository.findByRoomIdAndMemberId(roomId, managerId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 방장이 아니면 예외 발생
        if (!managerMate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }
        quitRoom(roomId, targetMemberId);
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
