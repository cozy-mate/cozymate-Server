package com.cozymate.cozymate_server.domain.friend.service;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.converter.FriendConverter;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendSummaryResponseDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendQueryService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public List<FriendSummaryResponseDTO> getFriendList(Long memberId) {

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

        return friendList.stream().map(
            friend -> friend.getSender().getId().equals(memberId) ?
                FriendConverter.toFriendSummaryResponseDTO(
                    friend.getReceiver(),
                    friend.getLikesReceiver()
                )
                :
                    FriendConverter.toFriendSummaryResponseDTO(
                        friend.getSender(),
                        friend.getLikesSender()
                    )
        ).toList();
    }

}
