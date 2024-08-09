package com.cozymate.cozymate_server.domain.friend.service;


import com.cozymate.cozymate_server.domain.friend.Friend;
import com.cozymate.cozymate_server.domain.friend.FriendRepository;
import com.cozymate.cozymate_server.domain.friend.converter.FriendConverter;
import com.cozymate.cozymate_server.domain.friend.dto.FriendRequestDTO;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendLikeResponseDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
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

    public Long requestFriend(Member sender, FriendRequestDTO friendRequestDTO) {

        // 요청을 전송하는 사람의 유효성을 검사
        Member receiver = memberRepository.findById(friendRequestDTO.getRequesterId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 요청하는 사람과 수락하는 사람이 같은지 검사
        if(sender.getId().equals(receiver.getId())){
             throw new GeneralException((ErrorStatus._FRIEND_REQUEST_EQUAL));
        }

        //요청 여부 양방향 체크
        if (friendRepository.existsBySenderIdAndReceiverId(sender.getId(), receiver.getId())) {
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_SENT);
        } else if (friendRepository.existsBySenderIdAndReceiverId(receiver.getId(),
            sender.getId())) {
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_RECEIVED);
        }
        //요청 여부가 없었다면, 친구 요청 전송
        Friend friendRequest = friendRepository.save(FriendConverter.toEntity(sender, receiver));
        return friendRequest.getId();
    }

    public Long acceptFriendRequest(Member receiver, FriendRequestDTO friendRequestDTO) {

        //요청을 보낸 사람의 유효성 검사
        Member sender = memberRepository.findById(friendRequestDTO.getRequesterId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if(sender.getId().equals(receiver.getId())){
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_EQUAL);
        }

        //친구 요청이 있었는지 검사
        Friend friendRequest = friendRepository.findBySenderIdAndReceiverId(sender.getId(),
                receiver.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._FRIEND_REQUEST_NOT_FOUND));

        friendRequest.accept();

        return friendRequest.getId();
    }

    public Long denyFriendRequest(Member receiver, FriendRequestDTO friendRequestDTO) {

        //요청을 보낸 사람의 유효성 검사
        Member sender = memberRepository.findById(friendRequestDTO.getRequesterId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if(sender.getId().equals(receiver.getId())){
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_EQUAL);
        }

        //친구 요청이 있었는지 검사
        Friend friendRequest = friendRepository.findBySenderIdAndReceiverId(sender.getId(),
                receiver.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._FRIEND_REQUEST_NOT_FOUND));

        // 이미 수락한 친구요청인지 검사
        if (friendRequest.getStatus().equals(FriendStatus.ACCEPT)) {
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_ACCEPTED);
        }

        friendRepository.delete(friendRequest);

        return friendRequest.getId();
    }

    public FriendLikeResponseDTO toggleLikeFriend(Member liker,
        FriendRequestDTO friendRequestDTO) {

        //요청을 받은 사람의 유효성을 검사
        Member receiver = memberRepository.findById(friendRequestDTO.getRequesterId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if(receiver.getId().equals(liker.getId())){
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_EQUAL);
        }

        // 친구 요청이 있었는지 양방향 검사
        Friend friendRequest = friendRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
                liker.getId(),
                receiver.getId(), liker.getId(), receiver.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._FRIEND_REQUEST_NOT_FOUND));

        // 대기중인 친구 요청일 경우 예외처리
        if (friendRequest.getStatus().equals(FriendStatus.WAITING)) {
            throw new GeneralException(ErrorStatus._FRIEND_REQUEST_WAITING);
        }
        // Friend Request안의 Sender라면
        if (friendRequest.getSender().getId().equals(liker.getId())) {
            friendRequest.toggleLikesReceiver();
            return FriendConverter.toFriendLikeResponseDTO(
                friendRequest.getReceiver(),
                friendRequest.isLikesReceiver()
            );
        }
        // Friend Request안의 Reciever라면
        friendRequest.toggleLikesSender();
        return FriendConverter.toFriendLikeResponseDTO(
            friendRequest.getSender(),
            friendRequest.isLikesSender()
        );
    }

}
