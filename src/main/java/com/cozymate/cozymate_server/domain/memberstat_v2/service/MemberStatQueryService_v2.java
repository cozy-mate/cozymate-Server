package com.cozymate.cozymate_server.domain.memberstat_v2.service;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.favorite.repository.FavoriteRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.util.MemberStatConverter_v2;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberStatQueryService_v2 {

    private final MateRepository mateRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberStatRepository_v2 memberStatRepository;


    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final RoomQueryService roomQueryService;


    private static final Long NO_ROOMMATE = 0L;
    private static final Long NOT_FAVORITE = 0L;

    @Transactional
    public MemberStatDetailWithMemberDetailResponseDTO getMemberStat(Member member) {
        MemberStatTest memberStat = memberStatRepository.findByMemberId(member.getId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );
        return MemberStatConverter_v2.toMemberStatDetailWithMemberDetailDTO(memberStat);
    }

    public MemberStatDetailAndRoomIdAndEqualityResponseDTO getMemberStatWithId(Member viewer,
        Long targetMemberId) {
        MemberStatTest memberStat = memberStatRepository.findByMemberId(targetMemberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        );

        Integer matchRate = lifestyleMatchRateService.getSingleMatchRate(viewer.getId(),
            targetMemberId);

        // 멤버 상세정보 보기의 리턴으로
        // roomId(없을 경우 0)
        // 본인이 속해 있는 방에 속해 있는 여부를 줘야 해서 아래 코드를 작성함.
        List<Mate> mateList = mateRepository.findByMemberIdAndEntryStatusInAndRoomStatusIn(
            targetMemberId,
            List.of(EntryStatus.PENDING, EntryStatus.JOINED),
            List.of(RoomStatus.ENABLE, RoomStatus.WAITING)
        );

        Long roomId = mateList.stream()
            .filter(mate -> mate.getEntryStatus().equals(EntryStatus.JOINED))
            .findFirst()
            .map(mate -> mate.getRoom().getId())
            .orElse(NO_ROOMMATE);

        // 자신이 속한 방에 요청을 했는가를 보여주는 변수(only 방장 입장)
        boolean hasRequestedRoomEntry = roomId.equals(NO_ROOMMATE)
            && roomQueryService.checkInvitationStatus(viewer, mateList);

        // 조회하는 사람을 좋아하는지 여부
        Long favoriteId = favoriteRepository.findByMemberAndTargetIdAndFavoriteType(viewer,
                targetMemberId, FavoriteType.MEMBER)
            .map(Favorite::getId)
            .orElse(NOT_FAVORITE);

        return MemberStatConverter_v2.toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
            memberStat, matchRate, roomId, hasRequestedRoomEntry, favoriteId
        );
    }

    public Integer getNumOfRoommateStatus(Long memberId) {
        return Integer.parseInt(memberStatRepository.findByMemberId(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
        ).getMemberUniversityStat().getNumberOfRoommate());
    }

//    public MemberStatPageResponseDTO<List<?>> getMemberStatList(Member member,
//        List<String> filterList, Pageable pageable) {
//
//        MemberStatTest criteriaMemberStat = getCriteriaMemberStat(member);
//
//        Slice<Map<MemberStatTest, Integer>> filteredResult = memberStatRepository.getFilteredMemberStat(
//            criteriaMemberStat,
//            filterList,
//            pageable
//        );
//
////        if (filteredResult.isEmpty()) {
////            return createEmptyPageResponse(pageable);
////        }
////
////        return toPageResponseDto(
////            createMemberStatPreferenceResponse(filteredResult, criteriaMemberStat));
//    }
//
//    private MemberStatTest getCriteriaMemberStat(Member member) {
//        return memberStatRepository.findByMemberId(member.getId());
//    }

}


