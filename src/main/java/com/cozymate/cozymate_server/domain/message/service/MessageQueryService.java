package com.cozymate.cozymate_server.domain.message.service;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.converter.MessageConverter;
import com.cozymate.cozymate_server.domain.message.dto.response.MessageContentResponseDTO;
import com.cozymate.cozymate_server.domain.message.dto.response.MessageListResponseDTO;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.message.validator.MessageValidator;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageQueryService {

    private final MessageRepositoryService messageRepositoryService;
    private final MessageRoomRepositoryService messageRoomRepositoryService;
    private final MessageValidator messageValidator;

    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";
    private static final String SELF_INDICATOR = " (나)";

    @Transactional
    public PageResponseDto<MessageListResponseDTO> getMessageList(Member member, Long messageRoomId,
        int page, int size) {
        MessageRoom messageRoom = messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoomId);

        messageValidator.checkMemberMisMatch(member, messageRoom);

        Slice<Message> messageList = getMessageList(messageRoom, member, page, size);

        updateLastSeenAt(member, messageRoom);

        List<MessageContentResponseDTO> messageResponseDtoList = toMessageResponseDTOList(messageList, member);

        Long recipientId = null;
        if (messageValidator.isMessageRoomActive(messageRoom)) {
            recipientId = messageValidator.isSameMember(messageRoom.getMemberA(), member)
                ? messageRoom.getMemberB().getId() : messageRoom.getMemberA().getId();
        }

        return PageResponseDto.<MessageListResponseDTO>builder()
            .page(page)
            .hasNext(messageList.hasNext())
            .result(MessageConverter.toMessageResponseDTO(recipientId, messageResponseDtoList))
            .build();
    }

    private Slice<Message> getMessageList(MessageRoom messageRoom, Member member, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        LocalDateTime lastDeleteAt = getMemberLastDeleteAt(messageRoom, member);

        if (messageValidator.isDeleteAtNull(lastDeleteAt)) {
            return messageRepositoryService.getMessageListByMessageRoom(messageRoom, pageRequest);
        }

        return messageRepositoryService.getMessageListByMessageRoomAndLastDeleteAt(messageRoom, lastDeleteAt,
            pageRequest);
    }

    private LocalDateTime getMemberLastDeleteAt(MessageRoom messageRoom, Member member) {
        if (messageValidator.isNullMember(messageRoom.getMemberA())) {
            return messageRoom.getMemberBLastDeleteAt();
        }

        if (messageValidator.isNullMember(messageRoom.getMemberB())) {
            return messageRoom.getMemberALastDeleteAt();
        }

        return messageValidator.isSameMember(messageRoom.getMemberA(), member)
            ? messageRoom.getMemberALastDeleteAt()
            : messageRoom.getMemberBLastDeleteAt();
    }

    private void updateLastSeenAt(Member member, MessageRoom messageRoom) {
        if (messageValidator.isNullMember(messageRoom.getMemberA())) {
            messageRoom.updateMemberBLastSeenAt();
            return;
        }

        if (messageValidator.isNullMember(messageRoom.getMemberB())) {
            messageRoom.updateMemberALastSeenAt();
            return;
        }

        if (messageValidator.isSameMember(messageRoom.getMemberA(), member)) {
            messageRoom.updateMemberALastSeenAt();
        } else {
            messageRoom.updateMemberBLastSeenAt();
        }
    }

    private List<MessageContentResponseDTO> toMessageResponseDTOList(Slice<Message> messageList,
        Member member) {
        return messageList.stream()
            .map(message -> {
                Member sender = message.getSender();

                if (messageValidator.isNullMember(sender)) {
                    String nickname = UNKNOWN_SENDER_NICKNAME;

                    return MessageConverter.toMessageContentResponseDTO(nickname, message.getContent(),
                        message.getCreatedAt());
                } else {
                    String nickname = sender.getNickname();
                    nickname = nickname.equals(member.getNickname())
                        ? nickname + SELF_INDICATOR
                        : nickname;

                    return MessageConverter.toMessageContentResponseDTO(nickname, message.getContent(),
                        message.getCreatedAt());
                }
            })
            .toList();
    }
}