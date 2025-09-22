package com.cozymate.cozymate_server.domain.messageroom.service;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.dto.MessageRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.CountMessageRoomsWithNewMessageDTO;
import com.cozymate.cozymate_server.domain.messageroom.converter.MessageRoomConverter;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.validator.MessageRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageRoomQueryService {

    private final MessageRoomRepositoryService messageRoomRepositoryService;
    private final MessageRepositoryService messageRepositoryService;
    private final MemberRepository memberRepository;
    private final MessageRoomValidator messageRoomValidator;

    private static final Integer NO_NEW_MESSAGE_ROOMS = 0;
    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";

    public PageResponseDto<List<MessageRoomDetailResponseDTO>> getMessageRoomList(Member member, int page,
        int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<Tuple> findMessageRoomList = messageRoomRepositoryService.getPagingMessageRoomListByMember(
            member, pageRequest);

        if (findMessageRoomList.isEmpty()) {
            return PageResponseDto.<List<MessageRoomDetailResponseDTO>>builder()
                .page(page)
                .hasNext(false)
                .result(List.of())
                .build();
        }

        List<MessageRoomDetailResponseDTO> messageRoomDetailResponseDTOList = findMessageRoomList.stream()
            .map(tuple -> {
                MessageRoom messageRoom = tuple.get("messageRoom", MessageRoom.class);
                Message lastMessage = tuple.get("lastMessage", Message.class);

                if (messageRoomValidator.isAnyMemberNullInMessageRoom(messageRoom)) {
                    return toMessageRoomDetailResponseDTO(messageRoom, member, lastMessage, false);
                }

                Member recipient = messageRoomValidator.isSameMember(messageRoom.getMemberA(), member)
                    ? messageRoom.getMemberB()
                    : messageRoom.getMemberA();

                LocalDateTime lastSeenAt =
                    messageRoomValidator.isSameMember(messageRoom.getMemberA(), member)
                        ? messageRoom.getMemberALastSeenAt()
                        : messageRoom.getMemberBLastSeenAt();

                boolean hasNewMessage = messageRoomValidator.existNewMessage(recipient, messageRoom,
                    lastSeenAt);

                return toMessageRoomDetailResponseDTO(messageRoom, member, lastMessage, hasNewMessage);
            }).toList();

        return PageResponseDto.<List<MessageRoomDetailResponseDTO>>builder()
            .page(page)
            .hasNext(findMessageRoomList.hasNext())
            .result(messageRoomDetailResponseDTOList)
            .build();
    }

    public CountMessageRoomsWithNewMessageDTO countMessageRoomsWithNewMessage(Member member) {
        List<MessageRoom> findMessageRoomList = messageRoomRepositoryService.getMessageRoomListByMember(member);

        if (findMessageRoomList.isEmpty()) {
            return MessageRoomConverter.toCountMessageRoomsWithNewMessageDTO(NO_NEW_MESSAGE_ROOMS);
        }

        List<MessageRoom> messageRoomList = findMessageRoomList.stream()
            .filter(messageRoom -> {
                Message message = getLatestMessageByMessageRoom(messageRoom);

                if (messageRoomValidator.isMessageNull(message)) {
                    return false;
                }

                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(messageRoom, member);
                return messageRoomValidator.isMessageReadable(lastDeleteAt, message);
            }).toList();

        long messageRoomsWithNewMessageCount = messageRoomList.stream()
            .filter(messageRoom -> {
                // 탈퇴 사용자가 있는 경우, 새로운 쪽지가 없음 처리
                if (messageRoomValidator.isAnyMemberNullInMessageRoom(messageRoom)) {
                    return false;
                }

                Member recipient = messageRoomValidator.isSameMember(messageRoom.getMemberA(), member)
                    ? messageRoom.getMemberB()
                    : messageRoom.getMemberA();

                LocalDateTime lastSeenAt =
                    messageRoomValidator.isSameMember(messageRoom.getMemberA(), member)
                        ? messageRoom.getMemberALastSeenAt()
                        : messageRoom.getMemberBLastSeenAt();

                return messageRoomValidator.existNewMessage(recipient, messageRoom, lastSeenAt);
            }).count();

        return MessageRoomConverter.toCountMessageRoomsWithNewMessageDTO((int) messageRoomsWithNewMessageCount);
    }

    public MessageRoomSimpleDTO getMessageRoom(Member member, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Optional<MessageRoom> findMessageRoom = messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(
            member, recipient);

        return MessageRoomConverter.toMessageRoomSimpleDTO(findMessageRoom, recipient);
    }

    private Message getLatestMessageByMessageRoom(MessageRoom messageRoom) {
        return messageRepositoryService.getLastMessageByMessageRoomOrNull(messageRoom);
    }

    private LocalDateTime getLastDeleteAtByMember(MessageRoom messageRoom, Member member) {
        // memberA가 null이면 memberB가 로그인 사용자임이 보장
        if (messageRoomValidator.isMemberNull(messageRoom.getMemberA())) {
            return messageRoom.getMemberBLastDeleteAt();
        }

        // memberB가 null이면 memberA가 로그인 사용자임이 보장
        if (messageRoomValidator.isMemberNull(messageRoom.getMemberB())) {
            return messageRoom.getMemberALastDeleteAt();
        }

        // 둘다 null이 아니면(탈퇴자가 없으면) 기존 로직 수행
        return messageRoom.getMemberA().getNickname().equals(member.getNickname())
            ? messageRoom.getMemberALastDeleteAt()
            : messageRoom.getMemberBLastDeleteAt();
    }

    private MessageRoomDetailResponseDTO toMessageRoomDetailResponseDTO(MessageRoom messageRoom, Member member,
        Message message, boolean hasNewChat) {

        // 상대가 탈퇴한 경우
        if (messageRoomValidator.isAnyMemberNullInMessageRoom(messageRoom)) {
            return MessageRoomConverter.toMessageRoomDetailResponseDTO(UNKNOWN_SENDER_NICKNAME,
                message.getContent(), messageRoom.getId());
        }

        return MessageRoomConverter.toMessageRoomDetailResponseDTO(member, message, messageRoom, hasNewChat);
    }
}