package com.cozymate.cozymate_server.domain.memberblock.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import com.cozymate.cozymate_server.domain.memberblock.converter.MemberBlockConverter;
import com.cozymate.cozymate_server.domain.memberblock.dto.response.MemberBlockResponseDTO;
import com.cozymate.cozymate_server.domain.memberblock.repository.MemberBlockRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBlockQueryService {

    private final MemberBlockRepository memberBlockRepository;

    public List<MemberBlockResponseDTO> getMemberBlockList(Member member) {
        List<MemberBlock> memberBlockList = memberBlockRepository.findByMemberId(member.getId());

        if (memberBlockList.isEmpty()) {
            return new ArrayList<>();
        }

        List<MemberBlockResponseDTO> memberBlockResponseDTOList = memberBlockList.stream()
            .map(memberBlock -> MemberBlockConverter.toMemberBlockResponseDTO(memberBlock.getBlockedMember()))
            .toList();

        return memberBlockResponseDTOList;
    }
}