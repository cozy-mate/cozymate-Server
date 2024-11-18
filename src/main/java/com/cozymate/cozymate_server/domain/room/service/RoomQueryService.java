package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityQueryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.response.InvitedRoomResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSearchResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final FavoriteRepository favoriteRepository;

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

        List<MateDetailResponseDTO> mates = joinedMates.stream()
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
            isFavoritedRoom(memberId, roomId),
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            getDormitoryName(room),
            room.getRoomType().toString(),
            hashtags,
            roomEquality,
            MemberStatConverter.toMemberStatDifferenceResponseDTO(mateMemberStats)
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

    public RoomIdResponseDTO getExistRoom(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));
        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom());
        }
        return RoomConverter.toRoomExistResponse(null);
    }

    public RoomIdResponseDTO getExistRoom(Long otherMemberId, Long memberId) {
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
        int sum = roomEquality.stream().mapToInt(Integer::intValue).sum();
        return roomEquality.isEmpty() ? null : sum / roomEquality.size();

    }

    public List<MateDetailResponseDTO> getInvitedMemberList(Long roomId, Long memberId) {
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

    public List<MateDetailResponseDTO> getPendingMemberList(Long managerId) {
        memberRepository.findById(managerId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 방장이 속한 방의 정보
        Room room = roomRepository.findById(getExistRoom(managerId).roomId())
            .orElseThrow(()-> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), managerId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        // 방장인지 검증
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        List<Mate> pendingMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(), EntryStatus.PENDING);

        Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(managerId,
            pendingMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));

        return pendingMates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toMateDetailListResponse(mate, mateEquality);
            }).toList();
    }

    public List<RoomDetailResponseDTO> getRequestedRoomList(Long memberId) {
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
                return RoomConverter.toRoomDetailResponseDTOWithParams(
                    room.getId(),
                    room.getName(),
                    room.getInviteCode(),
                    room.getProfileImage(),
                    joinedMates.stream()
                        .map(mate -> RoomConverter.toMateDetailListResponse(mate, equalityMap.get(mate.getMember().getId())))
                        .toList(),
                    getManagerMemberId(room),
                    getManagerNickname(room),
                    false,
                    isFavoritedRoom(memberId, room.getId()),
                    room.getMaxMateNum(),
                    room.getNumOfArrival(),
                    getDormitoryName(room),
                    room.getRoomType().toString(),
                    hashtags,
                    roomEquality,
                    MemberStatConverter.toMemberStatDifferenceResponseDTO(joinedMates.stream()
                        .map(mate -> memberStatRepository.findByMemberId(mate.getMember().getId()))
                        .flatMap(Optional::stream)
                        .toList())
                );
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

        List<RoomDetailResponseDTO> rooms = invitedRooms.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(), EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));
                Integer roomEquality = getCalculateRoomEquality(equalityMap);
                List<String> hashtags = roomHashtagRepository.findHashtagsByRoomId(room.getId());
                return RoomConverter.toRoomDetailResponseDTOWithParams(
                    room.getId(),
                    room.getName(),
                    room.getInviteCode(),
                    room.getProfileImage(),
                    joinedMates.stream()
                        .map(mate -> RoomConverter.toMateDetailListResponse(mate, equalityMap.get(mate.getMember().getId())))
                        .toList(),
                    getManagerMemberId(room),
                    getManagerNickname(room),
                    false,
                    isFavoritedRoom(memberId, room.getId()),
                    room.getMaxMateNum(),
                    room.getNumOfArrival(),
                    getDormitoryName(room),
                    room.getRoomType().toString(),
                    hashtags,
                    roomEquality,
                    MemberStatConverter.toMemberStatDifferenceResponseDTO(joinedMates.stream()
                        .map(mate -> memberStatRepository.findByMemberId(mate.getMember().getId()))
                        .flatMap(Optional::stream)
                        .toList())
                );
            })
            .toList();

        return new InvitedRoomResponseDTO(invitedCount, rooms);
    }

    public Long getManagerMemberId(Room room) {
        Mate managerMate = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        return managerMate.getMember().getId();
    }

    public String getManagerNickname(Room room) {
        Mate managerMate = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        return managerMate.getMember().getNickname();
    }

    public String getDormitoryName(Room room) {
        Mate managerMate = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        return managerMate.getMember().getMemberStat().getDormitoryName();
    }

    public Boolean getPendingStatus(Long roomId, Long memberId) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.PENDING);
    }

    public Boolean isFavoritedRoom(Long memberId, Long roomId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
        return favoriteRepository.existsByMemberAndTargetIdAndFavoriteType(member, roomId, FavoriteType.ROOM);
    }

    public List<RoomSearchResponseDTO> searchRooms(String keyword, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Long universityId = member.getUniversity().getId();
        Gender gender = member.getGender();

        if (memberStatRepository.existsByMemberId(memberId)) {
            List<Room> roomList = roomRepository.findMatchingPublicRooms(
                keyword,
                universityId,
                gender,
                member.getMemberStat().getNumOfRoommate(),
                member.getMemberStat().getDormitoryName()
            );

            return roomList.stream()
                .map(room -> {
                    List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(), EntryStatus.JOINED);
                    Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(
                        member.getId(),
                        joinedMates.stream().map(mate -> mate.getMember().getId()).toList()
                    );
                    return RoomConverter.toRoomSearchResponseDTO(
                        room,
                        getCalculateRoomEquality(equalityMap)
                    );
                })
                .sorted(Comparator.comparing(RoomSearchResponseDTO::equality, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        }

        // memberStat이 존재하지 않을 때
        List<Room> roomList = roomRepository.findMatchingPublicRooms(
            keyword,
            universityId,
            gender
        );

        return roomList.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(), EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = memberStatEqualityQueryService.getEquality(
                    member.getId(),
                    joinedMates.stream().map(mate -> mate.getMember().getId()).toList()
                );
                return RoomConverter.toRoomSearchResponseDTO(
                    room,
                    getCalculateRoomEquality(equalityMap)
                );
            })
            .sorted(Comparator.comparing(RoomSearchResponseDTO::name))
            .toList();
    }

}
