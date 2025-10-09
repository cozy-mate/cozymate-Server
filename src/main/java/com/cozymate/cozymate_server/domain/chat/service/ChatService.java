package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.fcm.event.converter.EventConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberCachingDTO;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.service.MemberCacheService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.WebSocketException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MemberCacheService memberCacheService;
    private final MemberRepositoryService memberRepositoryService;
    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final ChatRepositoryService chatRepositoryService;
    private final ApplicationEventPublisher eventPublisher;

    private static final int PAGE_SIZE = 20;

    @Transactional(transactionManager = "mongoTransactionManager")
    public void sendChat(CreateChatRequestDTO createChatRequestDTO) {
        if (!chatRoomRepositoryService.existsChatRoomMemberByChatRoomIdAndMemberId(
            createChatRequestDTO.chatRoomId(), createChatRequestDTO.memberId())) {
            throw new WebSocketException(ErrorStatus._CHATROOMMEMBER_NOT_FOUND);
        }

        // 채팅 저장
        Chat chat = ChatConverter.toDocument(createChatRequestDTO);
        chatRepositoryService.saveChat(chat);

        // sender의 nickname, persona 조회
        MemberCachingDTO memberCachingDTO = getMemberCachingDTO(createChatRequestDTO);

        // 트랜잭션 커밋 후 redis topic에 pub
        eventPublisher.publishEvent(ChatConverter.toChatPubDTO(chat, memberCachingDTO));

        // 푸시 알림
        eventPublisher.publishEvent(EventConverter.toSentChatEvent(createChatRequestDTO));
    }

    public ChatListResponseDTO getRecentChatList(ChatRoomMember chatRoomMember) {
        Long chatRoomId = chatRoomMember.getChatRoom().getId();
        LocalDateTime enterTime = chatRoomMember.getCreatedAt();

        List<Chat> chatList = chatRepositoryService.getChatListAfterEnterTime(chatRoomId, enterTime,
            getPageRequest());

        return convertToResponse(chatList);
    }

    public ChatListResponseDTO getChatListBeforeLastChatTime(Member member, Long chatRoomId,
        LocalDateTime lastChatTime, String chatId) {
        ChatRoomMember chatRoomMember = chatRoomRepositoryService.getChatRoomMemberByChatRoomIdAndMemberIdOrThrow(
            chatRoomId, member.getId());

        LocalDateTime enterTime = chatRoomMember.getCreatedAt();

        List<Chat> chatList = chatRepositoryService.getChatListInRange(chatRoomId, enterTime,
            lastChatTime, chatId, getPageRequest());

        return convertToResponse(chatList);
    }

    private MemberCachingDTO getMemberCachingDTO(CreateChatRequestDTO createChatRequestDTO) {
        MemberCachingDTO memberCachingDTO = memberCacheService.findMemberCachingDTO(
            createChatRequestDTO.memberId());

        // 캐시 miss 처리
        if (Objects.isNull(memberCachingDTO)) {
            Member member = memberRepositoryService.getMemberByIdOrSocketThrow(
                createChatRequestDTO.memberId());

            memberCachingDTO = MemberConverter.toMemberCachingDTO(member.getNickname(),
                member.getPersona());
            memberCacheService.saveMemberCachingDTO(member.getId(), memberCachingDTO);
        }

        return memberCachingDTO;
    }

    private PageRequest getPageRequest() {
        return PageRequest.of(0, PAGE_SIZE + 1,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
    }

    private ChatListResponseDTO convertToResponse(List<Chat> chatList) {
        boolean hasNext = chatList.size() > PAGE_SIZE;

        if (hasNext) {
            chatList = chatList.stream()
                .limit(PAGE_SIZE)
                .toList();
        }

        List<Long> memberIdList = chatList.stream()
            .map(chat -> chat.getMemberId())
            .distinct()
            .toList();

        Map<Long, MemberCachingDTO> cachingMemberMap = memberCacheService.getCachingMemberMap(
            memberIdList);

        List<ChatResponseDTO> chatResponseDTOList = chatList.stream()
            .map(chat -> ChatConverter.toChatResponseDTO(chat,
                cachingMemberMap.getOrDefault(chat.getMemberId(),
                    MemberConverter.toWithdrawMemberCachingDTO()).nickname(),
                cachingMemberMap.getOrDefault(chat.getMemberId(),
                    MemberConverter.toWithdrawMemberCachingDTO()).persona()))
            .toList();

        return ChatConverter.toChatListResponseDTO(hasNext, chatResponseDTOList);
    }
}
