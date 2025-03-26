package com.cozymate.cozymate_server.domain.memberfavorite.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.converter.MemberFavoriteConverter;
import com.cozymate.cozymate_server.domain.memberfavorite.dto.response.MemberFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepositoryService;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFavoriteQueryService {

    private final MemberFavoriteRepositoryService memberFavoriteRepositoryService;
    private final LifestyleMatchRateService lifestyleMatchRateService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;

    public PageResponseDto<List<MemberFavoriteResponseDTO>> getMemberFavoriteList(Member member,
        int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<MemberFavorite> memberFavoriteList = memberFavoriteRepositoryService.getMemberFavoriteListByMember(
            member, pageRequest);

        if (memberFavoriteList.isEmpty()) {
            return PageResponseDto.<List<MemberFavoriteResponseDTO>>builder()
                .page(page)
                .hasNext(false)
                .result(List.of())
                .build();
        }

        Map<Member, Long> targetMemberFavoriteIdMap = memberFavoriteList.stream()
            .collect(Collectors.toMap(MemberFavorite::getTargetMember, MemberFavorite::getId));

        List<Member> findTargetMemberList = new ArrayList<>(targetMemberFavoriteIdMap.keySet());

        Map<Long, Integer> equalityMap = lifestyleMatchRateService.getMatchRateWithMemberIdAndIdList(
            member.getId(),
            findTargetMemberList.stream().map(Member::getId).toList());

        List<String> criteriaPreferences = memberStatPreferenceQueryService.getPreferencesToList(
            member.getId());

        MemberStat memberStat = member.getMemberStat();

        List<MemberFavoriteResponseDTO> memberFavoriteResponseDTOList = findTargetMemberList.stream()
            .map(targetMember -> {
                    if (Objects.isNull(memberStat)) {
                        return MemberFavoriteConverter.toMemberFavoriteResponseDTO(
                            targetMemberFavoriteIdMap.get(targetMember),
                            MemberStatConverter.toPreferenceResponseDTO(
                                targetMember.getMemberStat(),
                                MemberStatConverter.toMemberStatPreferenceDetailWithoutColorDTOList(
                                    targetMember.getMemberStat(), criteriaPreferences), null));
                    }
                    return MemberFavoriteConverter.toMemberFavoriteResponseDTO(
                        targetMemberFavoriteIdMap.get(targetMember),
                        MemberStatConverter.toPreferenceResponseDTO(
                            targetMember.getMemberStat(),
                            MemberStatConverter.toMemberStatPreferenceDetailColorDTOList(
                                targetMember.getMemberStat(), member.getMemberStat(),
                                criteriaPreferences
                            ), equalityMap.get(targetMember.getId())));
                }
            ).toList();

        return PageResponseDto.<List<MemberFavoriteResponseDTO>>builder()
            .page(page)
            .hasNext(memberFavoriteList.hasNext())
            .result(memberFavoriteResponseDTOList)
            .build();
    }
}
