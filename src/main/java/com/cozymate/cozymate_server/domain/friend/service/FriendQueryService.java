package com.cozymate.cozymate_server.domain.friend.service;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.SimpleFriendResponseDTO;
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

    public List<SimpleFriendResponseDTO> getFriendList(Long memberId) {

        memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        List<Friend> friendList = friendRepository.findBySenderIdOrReceiverId(memberId, memberId);

        if (friendList.isEmpty()) {
            return new ArrayList<>();
        }

        return friendList.stream().map(
            friend -> friend.getSender().getId().equals(memberId) ?
                SimpleFriendResponseDTO
                    .builder()
                    .memberId(friend.getReceiver().getId())
                    .memberName(friend.getReceiver().getName())
                    .build() :
                SimpleFriendResponseDTO
                    .builder()
                    .memberId(friend.getSender().getId())
                    .memberName(friend.getSender().getName())
                    .build()
        ).toList();

    }

}
