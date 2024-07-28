package com.cozymate.cozymate_server.domain.friend.service;


import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.converter.FriendConverter;
import com.cozymate.cozymate_server.domain.friend.dto.FriendRequestDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class FriendCommandService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public Long requestFriend(Long senderId, FriendRequestDTO friendRequestDTO) {

        Member sender = memberRepository.findById(senderId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Member receiver = memberRepository.findById(friendRequestDTO.getReceiverId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Friend friendRequest = friendRepository.save(
            FriendConverter.toEntity(sender,receiver)
        );


        return friendRequest.getId();
    }

    public Long acceptFriendRequest(Long senderId, FriendRequestDTO friendRequestDTO) {
        Member sender = memberRepository.findById(senderId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Member receiver = memberRepository.findById(friendRequestDTO.getReceiverId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Friend friendRequest = friendRepository.findBySenderAndReceiver(sender,receiver).orElseThrow(
            () -> new GeneralException(ErrorStatus._FRIEND_REQUEST_NOT_FOUND)
        );

        friendRequest.accept();
        friendRepository.save(friendRequest);

        return friendRequest.getId();
    }

    public Long denyFriendRequest(Long senderId, FriendRequestDTO friendRequestDTO) {
        Member sender = memberRepository.findById(senderId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Member receiver = memberRepository.findById(friendRequestDTO.getReceiverId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
        );

        Friend friendRequest = friendRepository.findBySenderAndReceiver(sender,receiver).orElseThrow(
            () -> new GeneralException(ErrorStatus._FRIEND_REQUEST_NOT_FOUND)
        );

        friendRepository.delete(friendRequest);

        return friendRequest.getId();
    }

}
