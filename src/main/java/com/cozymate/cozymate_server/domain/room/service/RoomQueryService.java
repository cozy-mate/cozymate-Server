package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.converter.RoomConverter;
import com.cozymate.cozymate_server.domain.room.dto.response.InvitedRoomResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomExistResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSearchResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.room.util.RoomStatUtil;
import com.cozymate.cozymate_server.domain.room.validator.RoomValidator;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {

    private final MateRepository mateRepository;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatRepository memberStatRepository;
    private final RoomFavoriteRepository roomFavoriteRepository;
    private final MemberRepository memberRepository;
    private final RoomValidator roomValidator;
    private final RoomRepositoryService roomRepositoryService;
    private final MateRepositoryService mateRepositoryService;

    public RoomDetailResponseDTO getRoomById(Long roomId, Long memberId) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);
        roomValidator.checkRoomAccess(room, memberId);
        return processRoomDetails(room, memberId);
    }

    public RoomDetailResponseDTO getRoomByInviteCode(String inviteCode, Member member) {
        Room room = roomRepositoryService.getRoomByInviteCodeOrThrow(inviteCode);

        roomValidator.checkGender(room, member);
        return processRoomDetails(room, member.getId());
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
        memberRepository.findById(otherMemberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        return getExistRoom(otherMemberId);
    }

    public RoomExistResponseDTO getRoomExistInfo(Long memberId) {
        Optional<Mate> mate = mateRepository.findByMemberIdAndEntryStatusAndRoomStatusIn(
            memberId, EntryStatus.JOINED, List.of(RoomStatus.ENABLE, RoomStatus.WAITING));

        if (mate.isPresent()) {
            return RoomConverter.toRoomExistResponse(mate.get().getRoom(),
                mate.get().isRoomManager());
        }

        return RoomConverter.toRoomExistResponse(null, false);
    }

    public List<MateDetailResponseDTO> getInvitedMemberList(Long roomId, Long memberId) {
        Room room = roomRepositoryService.getRoomOrThrow(roomId);
        mateRepositoryService.getJoinedMateOrThrow(room.getId(), memberId);
        List<Mate> invitedMates = mateRepository.findByRoomIdAndEntryStatus(room.getId(),
            EntryStatus.INVITED);

        return getMateDetailResponseDTOS(memberId, invitedMates);

    }

    public List<MateDetailResponseDTO> getPendingMemberList(Long managerId) {
        // 방장이 속한 방의 정보
        Room room = roomRepositoryService.getRoomOrThrow(getExistRoom(managerId).roomId());
        Mate manager = mateRepositoryService.getJoinedMateOrThrow(room.getId(), managerId);

        // 방장인지 검증
        roomValidator.checkRoomManager(manager);

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

    public PageResponseDto<List<RoomDetailResponseDTO>> getPagedRoomList(Long memberId,
        EntryStatus entryStatus, Pageable pageable) {
        // 해당 memberId와 entryStatus에 맞는 방을 가져옴
        Slice<Room> roomSlice = roomRepositoryService.getRoomSliceByMemberIdAndEntryStatus(memberId,
            entryStatus, pageable);

        List<RoomDetailResponseDTO> content = convertToRoomDetailDTOs(roomSlice.getContent(),
            memberId);

        return PageResponseDto.<List<RoomDetailResponseDTO>>builder()
            .page(roomSlice.getNumber())
            .hasNext(roomSlice.hasNext())
            .result(content)
            .build();
    }

    public List<RoomDetailResponseDTO> getRoomList(Long memberId, EntryStatus entryStatus) {
        // 해당 memberId와 entryStatus에 맞는 방을 가져옴
        List<Room> rooms = roomRepositoryService.getRoomListByMemberIdAndEntryStatus(memberId,
            entryStatus);
        return convertToRoomDetailDTOs(rooms, memberId);
    }

    private List<RoomDetailResponseDTO> convertToRoomDetailDTOs(List<Room> rooms, Long memberId) {
        // 방장 정보를 맵으로 저장해둠
        Map<Long, Mate> managerMap = mateRepository.findRoomManagers(
            rooms.stream().map(Room::getId).toList()
        ).stream().collect(Collectors.toMap(mate -> mate.getRoom().getId(), mate -> mate));

        // 방 정보를 RoomDetailResponseDTO로 변환하여 반환
        return rooms.stream()
            .map(room -> {
                // 현재 방에 참여중인 메이트들
                List<Mate> joinedMates = mateRepository.findJoinedMatesWithMemberAndStats(
                    room.getId());

                // 일치율 매핑
                Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
                    memberId,
                    joinedMates.stream().map(mate -> mate.getMember().getId()).toList()
                );

                Integer roomEquality = RoomStatUtil.getCalculateRoomEquality(equalityMap);
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
                    Collections.emptyList(),
                    room.getDescription(),
                    roomEquality,
                    MemberStatConverter.toMemberStatDifferenceResponseDTO(
                        joinedMates.stream()
                            .map(mate -> memberStatRepository.findByMemberId(
                                mate.getMember().getId()))
                            .flatMap(Optional::stream)
                            .toList()
                    )
                );
            })
            .toList();
    }

    public PageResponseDto<List<RoomDetailResponseDTO>> getRequestedRoomList(Long memberId,
        int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getPagedRoomList(memberId, EntryStatus.PENDING, pageable);
    }

    public InvitedRoomResponseDTO getInvitedRoomList(Long memberId) {
        Integer invitedCount = mateRepository.countByMemberIdAndEntryStatus(memberId,
            EntryStatus.INVITED);
        return RoomConverter.toInvitedRoomResponseDTO(invitedCount,
            getRoomList(memberId, EntryStatus.INVITED));
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
        List<Room> roomList = roomRepositoryService.getRoomListByKeywordAndUniversityAndGender(
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

    public Boolean isMemberInEntryStatus(Long memberId, Member manager, EntryStatus entryStatus) {
        memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Room room = roomRepositoryService.getRoomOrThrow(getExistRoom(manager.getId()).roomId());
        Mate managerMate = mateRepositoryService.getJoinedMateOrThrow(room.getId(),
            manager.getId());

        // 방장인지 검증
        roomValidator.checkRoomManager(managerMate);

        return mateRepository.existsByRoomIdAndMemberIdAndEntryStatus(room.getId(), memberId,
            entryStatus);
    }

    public Boolean isEntryStatusToRoom(Long roomId, Long memberId, EntryStatus entryStatus) {
        roomRepositoryService.getRoomOrThrow(roomId);
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
            Collections.emptyList(),
            room.getDescription(),
            roomEquality,
            MemberStatConverter.toMemberStatDifferenceResponseDTO(
                mates.stream()
                    .map(mate -> memberStatRepository.findByMemberId(mate.memberId()))
                    .flatMap(Optional::stream)
                    .toList()
            )
        );
    }

    private Map<Long, Integer> calculateLifestyleMatchRates(Long viewerMemberId,
        List<Mate> joinedMates) {
        return lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
            viewerMemberId,
            joinedMates.stream()
                .map(mate -> mate.getMember().getId())
                .collect(Collectors.toList())
        );
    }

}
