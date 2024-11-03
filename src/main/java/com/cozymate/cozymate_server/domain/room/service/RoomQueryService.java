package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.CozymateInfoResponse;
import com.cozymate.cozymate_server.domain.room.dto.CozymateResponse;
import com.cozymate.cozymate_server.domain.room.dto.InviteRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomCreateResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomExistResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final RoomHashtagRepository roomHashtagRepository;
    private final MemberStatEqualityQueryService memberStatEqualityQueryService;
    private final MemberStatRepository memberStatRepository;

    public RoomCreateResponse getRoomById(Long roomId, Long memberId) {

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (room.getRoomType()== RoomType.PRIVATE) {
            mateRepository.findByRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
        }

        Mate creatingMate = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Boolean isRoomManager = creatingMate.getMember().getId().equals(memberId);

        List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(roomId, EntryStatus.JOINED);

        Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
            joinedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));

        Integer roomEquality = getCalculateRoomEquality(memberId, equalityMap);

        List<CozymateInfoResponse> mates = joinedMates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toCozymateInfoResponse(mate, mateEquality);
            }).toList();

        // MemberStat이 null일 때 제외했음.
        List<MemberStat> mateMemberStats = mates.stream()
            .map(mate -> memberStatRepository.findByMemberId(mate.getMemberId()))
            .flatMap(Optional::stream)
            .toList();

        //해시태그 가져오기
        List<String> hashtags = roomHashtagRepository.findHashtagsByRoomId(roomId);

        return new RoomCreateResponse(room.getId(), room.getName(), room.getInviteCode(), room.getProfileImage(),
            mates.isEmpty() ? new ArrayList<>() : mates,
            creatingMate.getMember().getId(),
            isRoomManager,
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            room.getRoomType(),
            hashtags,
            roomEquality,
            MemberStatConverter.toMemberStatDifferenceResponseDTO(mateMemberStats)
            // Todo: 기숙사 정보 추가
            );
    }

    public RoomJoinResponse getRoomByInviteCode(String inviteCode, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate managerMate = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Member manager = memberRepository.findById(managerMate.getMember().getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return RoomConverter.toRoomJoinResponse(room, manager);
    }

    public List<CozymateResponse> getCozymateList(Long roomId, Long memberId) {
        roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        List<Friend> friendList = friendRepository.findBySenderIdOrReceiverId(memberId, memberId)
            .stream()
            .filter(friendRequest -> friendRequest.getStatus().equals(FriendStatus.ACCEPT))
            .toList();

        if (friendList.isEmpty()) {
            return new ArrayList<>();
        }

        return friendList.stream()
            .filter(friend -> {
                Long friendMemberId = friend.getSender().getId().equals(memberId) ?
                    friend.getReceiver().getId() :
                    friend.getSender().getId();

                // 이미 참여한 방이 있는 cozymate는 제외
                return !mateRepository.existsByMemberIdAndRoomStatuses(friendMemberId,
                    RoomStatus.ENABLE, RoomStatus.WAITING);
            })
            .map(friend -> friend.getSender().getId().equals(memberId) ?
                RoomConverter.toCozymateResponse(friend.getReceiver()) :
                RoomConverter.toCozymateResponse(friend.getSender()))
            .toList();
    }

    public InviteRequest getInvitation(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
        Mate mate = mateRepository.findByMemberIdAndEntryStatus(memberId, EntryStatus.PENDING)
            .orElseThrow(()-> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));
        Room room = roomRepository.findById(mate.getRoom().getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        return RoomConverter.toInviteRequest(room, mate);
    }

    public Boolean isValidRoomName(String roomName) {
        return !roomRepository.existsByName(roomName);
    }

    public RoomExistResponse getExistRoom(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));
        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom());
        } else {
            return RoomConverter.toRoomExistResponse(null);
        }
    }

    public Integer getCalculateRoomEquality(Long memberId, Map<Long, Integer> equalityMap){
        List<Integer> roomEquality = equalityMap.entrySet().stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        if (roomEquality.isEmpty()) {
            return 0;
        }

        return (int) Math.round(roomEquality.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0));

    }




}
