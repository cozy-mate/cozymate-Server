package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
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
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepository;
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
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepository memberStatRepository;
    private final RoomFavoriteRepository roomFavoriteRepository;
    private final RoomHashtagRepository roomHashtagRepository;

    public RoomDetailResponseDTO getRoomById(Long roomId, Long memberId) {
        Room room = validateRoom(roomId);
        validateRoomAccess(room, memberId);
        return processRoomDetails(room, memberId);
    }

    public RoomDetailResponseDTO getRoomByInviteCode(String inviteCode, Long memberId) {
        Room room = roomRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
        return processRoomDetails(room, memberId);
    }

    public Boolean isValidRoomName(String roomName) {
        return !roomRepository.existsByName(roomName);
    }

    public RoomIdResponseDTO getExistRoom(Long memberId) {
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));
        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom());
        }
        return RoomConverter.toRoomExistResponse(null);
    }

    public RoomIdResponseDTO getExistRoom(Long otherMemberId, Long memberId) {
        return getExistRoom(otherMemberId);
    }

    public List<MateDetailResponseDTO> getInvitedMemberList(Long roomId, Long memberId) {
        Room room = validateRoom(roomId);
        validateRoomMember(room.getId(), memberId);
        List<Mate> invitedMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(),
            EntryStatus.INVITED);

        return getMateDetailResponseDTOS(memberId, invitedMates);

    }

    public List<MateDetailResponseDTO> getPendingMemberList(Long managerId) {
        // 방장이 속한 방의 정보
        Room room = validateRoom(getExistRoom(managerId).roomId());

        // 방장인지 검증
        validateRoomManager(room.getId(), managerId);

        List<Mate> pendingMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(),
            EntryStatus.PENDING);

        return getMateDetailResponseDTOS(managerId, pendingMates);
    }

    private List<MateDetailResponseDTO> getMateDetailResponseDTOS(Long managerId,
        List<Mate> Mates) {
        Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
            managerId,
            Mates.stream().map(mate -> mate.getMember().getId()).collect(Collectors.toList()));

        return Mates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toMateDetailListResponse(mate, mateEquality);
            }).toList();
    }


    public List<RoomDetailResponseDTO> getRoomList(Long memberId, EntryStatus entryStatus) {
        // 해당 memberId와 entryStatus에 맞는 방을 가져옴
        List<Room> rooms = roomRepository.findRoomsWithMates(memberId, entryStatus);

        // 방 해시태그 정보 저장
        Map<Long, List<String>> roomHashtagsMap = getRoomHashtagsMap(rooms);

        // 방장 정보를 맵으로 저장해둠
        Map<Long, Mate> managerMap = mateRepository.findRoomManagers(rooms.stream().map(Room::getId).toList())
            .stream()
            .collect(Collectors.toMap(mate -> mate.getRoom().getId(), mate -> mate));

        // 방 정보를 RoomDetailResponseDTO로 변환하여 반환
        return rooms.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findJoinedMatesWithMemberAndStats(room.getId());
                Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                    memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId())
                        .collect(Collectors.toList()));
                Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);
                List<String> hashtags = extractHashtags(room, roomHashtagsMap);
                Mate managerMate = managerMap.get(room.getId());
                return RoomConverter.toRoomDetailResponseDTOWithParams(
                    room.getId(),
                    room.getName(),
                    room.getInviteCode(),
                    room.getProfileImage(),
                    joinedMates.stream()
                        .map(mate -> RoomConverter.toMateDetailListResponse(mate,
                            equalityMap.get(mate.getMember().getId())))
                        .toList(),
                    managerMate.getMember().getId(),
                    managerMate.getMember().getNickname(),
                    false,
                    isFavoritedRoom(memberId, room.getId()),
                    room.getMaxMateNum(),
                    room.getNumOfArrival(),
                    getDormitoryName(managerMate),
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


    public List<RoomDetailResponseDTO> getRequestedRoomList(Long memberId) {
        return getRoomList(memberId, EntryStatus.PENDING);
    }

    public InvitedRoomResponseDTO getInvitedRoomList(Long memberId) {
        Integer invitedCount = mateRepository.countByMemberIdAndEntryStatus(memberId,
            EntryStatus.INVITED);
        return RoomConverter.toInvitedRoomResponseDTO(invitedCount, getRoomList(memberId, EntryStatus.INVITED));
    }

    public String getDormitoryName(Mate managerMate) {
        return Optional.ofNullable(managerMate.getMember().getMemberStat())
            .map(memberStat -> memberStat.getMemberUniversityStat().getDormitoryName())
            .orElse("");
    }

    public Long isFavoritedRoom(Long memberId, Long roomId) {
        return roomFavoriteRepository.findByMemberIdAndRoomId(memberId, roomId)
            .map(RoomFavorite::getId)
            .orElse(0L);
    }

    private void validateRoomAccess(Room room, Long memberId) {
        if (room.getRoomType() == RoomType.PRIVATE) {
            validateRoomMember(room.getId(), memberId);
        }
    }

    private void validateRoomMember(Long roomId, Long memberId) {
        mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
    }

    private void validateRoomManager(Long roomId, Long managerId) {
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }
    }

    public List<RoomSearchResponseDTO> searchRooms(String keyword, Member member) {
        Long universityId = member.getUniversity().getId();
        Gender gender = member.getGender();

//        if (memberStatRepository.existsByMemberId(memberId)) {
//            List<Room> roomList = roomRepository.findMatchingPublicRooms(
//                keyword,
//                universityId,
//                gender,
//                member.getMemberStat().getNumOfRoommate(),
//                member.getMemberStat().getDormitoryName()
//            );

        // 학교, 성별로만 필터링
        List<Room> roomList = roomRepository.findMatchingPublicRooms(
            keyword,
            universityId,
            gender
        );

        // memberstat 있는 경우 일치율 순으로 정렬
        if (memberStatRepository.existsByMemberId(member.getId())) {
            return roomList.stream()
                .map(room -> {
                    List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(
                        room.getId(), EntryStatus.JOINED);
                    Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                        member.getId(),
                        joinedMates.stream().map(mate -> mate.getMember().getId()).toList()
                    );
                    return RoomConverter.toRoomSearchResponseDTO(
                        room,
                        RoomStatUtil.getCalculateRoomEquality(equalityMap)
                    );
                })
                .sorted(Comparator.comparing(RoomSearchResponseDTO::equality,
                    Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        }

        // memberstat 없는 경우 가나다 순으로 정렬
        return roomList.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(),
                    EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                    member.getId(),
                    joinedMates.stream().map(mate -> mate.getMember().getId()).toList()
                );
                return RoomConverter.toRoomSearchResponseDTO(
                    room,
                    RoomStatUtil.getCalculateRoomEquality(equalityMap)
                );
            })
            .sorted(Comparator.comparing(RoomSearchResponseDTO::name))
            .toList();
    }

    public boolean checkInvitationStatus(Member viewer, List<Mate> mates) {
        return mates.stream()
            .filter(m -> m.getEntryStatus().equals(EntryStatus.PENDING))
            .anyMatch(m -> mateRepository.findByMemberAndEntryStatus(viewer, EntryStatus.JOINED)
                .filter(viewerMate ->
                    viewerMate.isRoomManager() &&
                        viewerMate.getRoom().equals(m.getRoom()))
                .isPresent());
    }

    public Boolean isMemberInEntryStatus(Long memberId, Long managerId, EntryStatus entryStatus) {
        Room room = validateRoom(getExistRoom(managerId).roomId());

        // 방장인지 검증
        validateRoomManager(room.getId(), managerId);

        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(room.getId(), memberId,
            entryStatus);
    }

    public Boolean isEntryStatusToRoom(Long roomId, Long memberId, EntryStatus entryStatus) {
        validateRoom(roomId);
        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
            entryStatus);
    }

    private RoomDetailResponseDTO processRoomDetails(Room room, Long viewerMemberId) {
        // 방에 참여중인 메이트 정보 가져옴
        List<Mate> joinedMates = mateRepository.findJoinedMatesWithMemberAndStats(room.getId());

        Mate managerMate = joinedMates.stream()
            .filter(Mate::isRoomManager)
            .findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Member manager = managerMate.getMember();
        Boolean isRoomManager = manager.getId().equals(viewerMemberId);

        // 라이프스타일 매칭 계산
        Map<Long, Integer> equalityMap = calculateLifestyleMatchRates(viewerMemberId, joinedMates);
        Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);

        List<MateDetailResponseDTO> mates = joinedMates.stream()
            .map(mate -> {
                Integer mateEquality = equalityMap.get(mate.getMember().getId());
                return RoomConverter.toMateDetailListResponse(mate, mateEquality);
            }).toList();

        Map<Long, List<String>> roomHashtagsMap = getRoomHashtagsMap(List.of(room));
        List<String> hashtags = extractHashtags(room, roomHashtagsMap);

        return RoomConverter.toRoomDetailResponseDTOWithParams(
            room.getId(),
            room.getName(),
            room.getInviteCode(),
            room.getProfileImage(),
            mates.isEmpty() ? new ArrayList<>() : mates,
            manager.getId(),
            manager.getNickname(),
            isRoomManager,
            isFavoritedRoom(viewerMemberId, room.getId()),
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            getDormitoryName(managerMate),
            room.getRoomType().toString(),
            hashtags,
            roomEquality,
            MemberStatConverter.toMemberStatDifferenceResponseDTO(
                mates.stream()
                    .map(mate -> memberStatRepository.findByMemberId(mate.memberId()))
                    .flatMap(Optional::stream)
                    .toList()
            )
        );
    }

    private Map<Long, Integer> calculateLifestyleMatchRates(Long viewerMemberId, List<Mate> joinedMates) {
        return lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
            viewerMemberId,
            joinedMates.stream()
                .map(mate -> mate.getMember().getId())
                .collect(Collectors.toList())
        );
    }

    private Map<Long, List<String>> getRoomHashtagsMap(List<Room> rooms) {
        return roomHashtagRepository.findByRoomIds(
                rooms.stream().map(Room::getId).toList()
            ).stream()
            .collect(Collectors.groupingBy(
                roomHashtag -> roomHashtag.getRoom().getId(),
                Collectors.mapping(roomHashtag -> roomHashtag.getHashtag().getName(), Collectors.toList())
            ));
    }

    private List<String> extractHashtags(Room room, Map<Long, List<String>> roomHashtagsMap) {
        return roomHashtagsMap.getOrDefault(room.getId(), List.of());
    }

    private Room validateRoom(Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
    }
}
