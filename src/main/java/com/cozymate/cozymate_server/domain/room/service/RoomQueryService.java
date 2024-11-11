package com.cozymate.cozymate_server.domain.room.service;

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
import com.cozymate.cozymate_server.domain.room.dto.response.InvitedRoomResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailListReponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomListResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSimpleResponseDTO;
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
    private final RoomHashtagRepository roomHashtagRepository;
    private final MemberStatEqualityQueryService memberStatEqualityQueryService;
    private final MemberStatRepository memberStatRepository;

    public RoomDetailResponseDTO getRoomById(Long roomId, Long memberId) {

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (room.getRoomType()== RoomType.PRIVATE) {
            mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
        }

        Mate creatingMate = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Boolean isRoomManager = creatingMate.getMember().getId().equals(memberId);

        List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(roomId, EntryStatus.JOINED);

        Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
            joinedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));

        Integer roomEquality = getCalculateRoomEquality(equalityMap);

        List<MateDetailListReponseDTO> mates = joinedMates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toMateDetailListResponse(mate, mateEquality);
            }).toList();

        // MemberStat이 null일 때 제외했음.
        List<MemberStat> mateMemberStats = mates.stream()
            .map(mate -> memberStatRepository.findByMemberId(mate.memberId()))
            .flatMap(Optional::stream)
            .toList();

        //해시태그 가져오기
        List<String> hashtags = roomHashtagRepository.findHashtagsByRoomId(roomId);

        return RoomConverter.toRoomDetailResponseDTOWithParams(room.getId(), room.getName(), room.getInviteCode(), room.getProfileImage(),
            mates.isEmpty() ? new ArrayList<>() : mates,
            creatingMate.getMember().getId(),
            creatingMate.getMember().getNickname(),
            isRoomManager,
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            room.getRoomType().toString(),
            hashtags,
            roomEquality,
            MemberStatConverter.toMemberStatDifferenceResponseDTO(mateMemberStats)
            // Todo: 기숙사 정보 추가
        );
    }

    public RoomDetailResponseDTO getRoomByInviteCode(String inviteCode, Long memberId) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        Mate managerMate = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Member manager = memberRepository.findById(managerMate.getMember().getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return getRoomById(room.getId(), manager.getId());
    }

    public Boolean isValidRoomName(String roomName) {
        return !roomRepository.existsByName(roomName);
    }

    public RoomSimpleResponseDTO getExistRoom(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));
        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom());
        }
        return RoomConverter.toRoomExistResponse(null);
    }

    public RoomSimpleResponseDTO getExistRoom(Long otherMemberId, Long memberId) {
        memberRepository.findById(otherMemberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            otherMemberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));
        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom());
        }

        return RoomConverter.toRoomExistResponse(null);

    }

    public Integer getCalculateRoomEquality(Map<Long, Integer> equalityMap){
        List<Integer> roomEquality = equalityMap.values().stream()
            .toList();

        if (roomEquality.isEmpty()) {
            return 0;
        }

        return (int) Math.round(roomEquality.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0));

    }

    public List<MateDetailListReponseDTO> getInvitedMemberList(Long roomId, Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        List<Mate> invitedMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(), EntryStatus.INVITED);

        Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
            invitedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));

        return invitedMates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toMateDetailListResponse(mate, mateEquality);
            }).toList();

    }

    public List<RoomListResponseDTO> getRequestedRoomList(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        List<Room> requestedRooms = mateRepository.findAllByMemberIdAndEntryStatus(memberId, EntryStatus.PENDING)
            .stream()
            .map(Mate::getRoom)
            .toList();

        return requestedRooms.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(), EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));
                Integer roomEquality = getCalculateRoomEquality(equalityMap);
                List<String> hashtags = roomHashtagRepository.findHashtagsByRoomId(room.getId());
                return RoomConverter.toRoomListResponse(room, roomEquality, hashtags);
            })
            .toList();
    }

    public InvitedRoomResponseDTO getInvitedRoomList(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Integer invitedCount = mateRepository.countByMemberIdAndEntryStatus(memberId, EntryStatus.INVITED);

        List<Room> invitedRooms = mateRepository.findAllByMemberIdAndEntryStatus(memberId, EntryStatus.INVITED)
            .stream()
            .map(Mate::getRoom)
            .toList();

        List<RoomListResponseDTO> rooms = invitedRooms.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(), EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));
                Integer roomEquality = getCalculateRoomEquality(equalityMap);
                List<String> hashtags = roomHashtagRepository.findHashtagsByRoomId(room.getId());
                return RoomConverter.toRoomListResponse(room, roomEquality, hashtags);
            })
            .toList();

        return new InvitedRoomResponseDTO(invitedCount, rooms);
    }
}
