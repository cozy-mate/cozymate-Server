package com.cozymate.cozymate_server.domain.friend.service;

import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.converter.FriendConverter;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendSummaryResponseDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendQueryService {

    private final FriendRepository friendRepository;

    public List<FriendSummaryResponseDTO> getFriendList(Member member) {

        List<Friend> friendList = friendRepository.findBySenderIdOrReceiverId(member.getId(), member.getId())
            .stream()
            .filter(friendRequest -> friendRequest.getStatus().equals(FriendStatus.ACCEPT))
            .toList();

        if (friendList.isEmpty()) {
            return new ArrayList<>();
        }

        return friendList.stream().map(
            friend -> friend.getSender().getId().equals(member.getId()) ?
                FriendConverter.toFriendSummaryResponseDTO(
                    friend.getReceiver(),
                    friend.isLikesSender()
                )
                :
                    FriendConverter.toFriendSummaryResponseDTO(
                        friend.getSender(),
                        friend.isLikesSender()
                    )
        ).toList();
    }

    public String getFriendStatus(Member member, Long friendId) {
        // 요청하는 사람과 수락하는 사람이 같은지 검사
        if(member.getId().equals(friendId)){
            throw new GeneralException((ErrorStatus._FRIEND_REQUEST_EQUAL));
        }
        //친구 요청이 존재하는지 검사
        Optional<Friend> friendRequest = friendRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            member.getId(), friendId, friendId, member.getId()
        );
        if(friendRequest.isPresent()){
            return friendRequest.get().getStatus().toString();
        }
        return FriendStatus.STRANGER.toString();
    }

}
