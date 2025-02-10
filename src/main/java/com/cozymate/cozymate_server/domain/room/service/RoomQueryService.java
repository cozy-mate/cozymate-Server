package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
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
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.Collections;
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
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepository memberStatRepository;
    private final RoomFavoriteRepository roomFavoriteRepository;

    public RoomDetailResponseDTO getRoomById(Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (room.getRoomType() == RoomType.PRIVATE) {
            mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                    EntryStatus.JOINED)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
        }

        Mate creatingMate = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        Boolean isRoomManager = creatingMate.getMember().getId().equals(memberId);

        List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);

        Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
            memberId,
            joinedMates.stream().map(mate -> mate.getMember().getId())
                .collect(Collectors.toList()));

        Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);

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

        // 해시태그 가져오기
        List<String> hashtags = Optional.ofNullable(room.getRoomHashtags())
            .orElse(Collections.emptyList()) // null인 경우에 빈 리스트 반환
            .stream()
            .map(RoomHashtag::getHashtag)
            .map(Hashtag::getHashtag)
            .toList();

        return RoomConverter.toRoomDetailResponseDTOWithParams(room.getId(), room.getName(),
            room.getInviteCode(), room.getProfileImage(),
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
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return getExistRoom(otherMemberId);
    }

    public List<MateDetailResponseDTO> getInvitedMemberList(Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), memberId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        List<Mate> invitedMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(),
            EntryStatus.INVITED);

        return getMateDetailResponseDTOS(memberId, invitedMates);

    }

    public List<MateDetailResponseDTO> getPendingMemberList(Long managerId) {
        // 방장이 속한 방의 정보
        Room room = roomRepository.findById(getExistRoom(managerId).roomId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 방장인지 검증
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

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
        List<Room> rooms = mateRepository.findAllByMemberIdAndEntryStatus(memberId, entryStatus)
            .stream()
            .map(Mate::getRoom)
            .toList();

        // 방 정보를 RoomDetailResponseDTO로 변환하여 반환
        return rooms.stream()
            .map(room -> {
                List<Mate> joinedMates = mateRepository.findAllByRoomIdAndEntryStatus(room.getId(),
                    EntryStatus.JOINED);
                Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                    memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId())
                        .collect(Collectors.toList()));
                Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);
                List<String> hashtags = Optional.ofNullable(room.getRoomHashtags())
                    .orElse(Collections.emptyList()) // null인 경우에 빈 리스트 반환
                    .stream()
                    .map(RoomHashtag::getHashtag)
                    .map(Hashtag::getHashtag)
                    .toList();
                return RoomConverter.toRoomDetailResponseDTOWithParams(
                    room.getId(),
                    room.getName(),
                    room.getInviteCode(),
                    room.getProfileImage(),
                    joinedMates.stream()
                        .map(mate -> RoomConverter.toMateDetailListResponse(mate,
                            equalityMap.get(mate.getMember().getId())))
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


    public List<RoomDetailResponseDTO> getRequestedRoomList(Long memberId) {
        return getRoomList(memberId, EntryStatus.PENDING);
    }

    public InvitedRoomResponseDTO getInvitedRoomList(Long memberId) {
        Integer invitedCount = mateRepository.countByMemberIdAndEntryStatus(memberId,
            EntryStatus.INVITED);
        return new InvitedRoomResponseDTO(invitedCount, getRoomList(memberId, EntryStatus.INVITED));
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
        return Optional.ofNullable(managerMate.getMember().getMemberStat())
            .map(membetStat -> membetStat.getMemberUniversityStat().getDormitoryName())
            .orElse(""); // MemberStat이 null일 경우 빈 문자열 반환
    }

    public Long isFavoritedRoom(Long memberId, Long roomId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        Room room = roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        return roomFavoriteRepository.findByMemberAndRoom(member, room)
            .map(RoomFavorite::getId)
            .orElse(0L);
    }

    public List<RoomSearchResponseDTO> searchRooms(String keyword, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
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
        if (memberStatRepository.existsByMemberId(memberId)) {
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
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepository.findById(getExistRoom(managerId).roomId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        // 방장인지 검증
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(room.getId(), memberId,
            entryStatus);
    }

    public Boolean isEntryStatusToRoom(Long roomId, Long memberId, EntryStatus entryStatus) {
        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
            entryStatus);
    }
}
