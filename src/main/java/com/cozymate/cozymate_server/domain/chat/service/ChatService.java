package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatStreamDTO;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDTO;
import com.cozymate.cozymate_server.domain.chat.service.redis.ChatStreamService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.global.redispubsub.RedisPublisher;
import com.cozymate.cozymate_server.domain.fcm.event.converter.EventConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberCachingDTO;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.service.MemberCacheService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.WebSocketException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatStreamService chatStreamService;
    private final RedisPublisher redisPublisher;
    private final MemberCacheService memberCacheService;
    private final MemberRepositoryService memberRepositoryService;
    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final ChatRepositoryService chatRepositoryService;
    private final ApplicationEventPublisher eventPublisher;

    private static final int PAGE_SIZE = 20;

    public void sendChat(CreateChatRequestDTO createChatRequestDTO) {
        if (!chatRoomRepositoryService.existsChatRoomMemberByChatRoomIdAndMemberId(
            createChatRequestDTO.chatRoomId(), createChatRequestDTO.memberId())) {
            throw new WebSocketException(ErrorStatus._CHATROOMMEMBER_NOT_FOUND);
        }

        // Redis stream에 생산 -> stream 컨슈머가 mongo에 저장
        produceToStream(createChatRequestDTO);

        // sender의 nickname, persona 조회
        MemberCachingDTO memberCachingDTO = getMemberCachingDTO(createChatRequestDTO);

        // redis topic에 pub
        redisPublisher.publishToChat(
            ChatConverter.toChatPubDTO(createChatRequestDTO, memberCachingDTO));

        // 푸시 알림
        eventPublisher.publishEvent(EventConverter.toSentChatEvent(createChatRequestDTO));
    }

    public void saveChat(ChatStreamDTO dto, LocalDateTime createdAt, Long sequence) {
        Chat chat = ChatConverter.toDocument(dto, createdAt, sequence);
        chatRepositoryService.saveChat(chat);
    }

    public ChatListResponseDTO getChatListBeforeLastChatTime(Member member, Long chatRoomId,
        LocalDateTime lastChatTime, Long sequence) {
        ChatRoomMember chatRoomMember = chatRoomRepositoryService.getChatRoomMemberByChatRoomIdAndMemberIdOrThrow(
            chatRoomId, member.getId());

        // 해당 채팅방에 최초에 입장한 시간
        LocalDateTime enterTime = chatRoomMember.getCreatedAt();

        // Mongo에서 조회
        List<Chat> chatList = findChatListBeforeLastChatTime(chatRoomId, lastChatTime, sequence,
            enterTime);

        boolean hasNext = chatList.size() > PAGE_SIZE;

        if (hasNext) {
            chatList = chatList.stream()
                .limit(PAGE_SIZE)
                .toList();
        }

        return convertToResponse(chatList, hasNext);
    }

    /**
     * 방 입장 시는 무조건 stream에서 조회, 필요시 몽고 추가 조회
     */
    public ChatListResponseDTO getRecentChatList(ChatRoomMember chatRoomMember) {
        Long chatRoomId = chatRoomMember.getChatRoom().getId();
        LocalDateTime enterTime = chatRoomMember.getCreatedAt();

        // redis stream에서 최신 채팅 20 + 1개를 조회
        List<Chat> findByRedisStream = chatStreamService.getRecent21ChatList(chatRoomId);

        // 조회 사용자의 입장 시각 기준 이후에 생성된 chat만 필터
        List<Chat> chatList = findByRedisStream.stream()
            .filter(chat -> chat.getCreatedAt().isAfter(enterTime))
            .collect(Collectors.toList()); // mutable 리스트로 변환 (몽고 조회 추가해야하는 경우 존재)

        boolean hasNext = chatList.size() > PAGE_SIZE;
        if (hasNext) {
            chatList = chatList.stream()
                .limit(PAGE_SIZE)
                .toList();
        } else {
            if (findByRedisStream.size() < PAGE_SIZE) { // 애초에 redis에서 가져와진 데이터가 20개보다 적엇다면
                String minIdPrevId = chatStreamService.getMinIdPrevId(
                    chatStreamService.generateChatroomStreamKey(chatRoomId));

                if (Objects.nonNull(minIdPrevId)) { // pending 기준 trim 발생으로 짤린 데이터의 마지막 시간이 redis에 존재한다면
                    log.info("trim 연산으로 인한 prev recordId 조회 : {}", minIdPrevId);
                    String[] split = minIdPrevId.split("-");
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(split[0]));
                    LocalDateTime prevCreatedAt = LocalDateTime.ofInstant(instant,
                        ZoneId.systemDefault());

                    // trim minId 바로 전 message의 생성일이 입장 시간 이후이면, 몽고에서 추가 조회 필요
                    if (prevCreatedAt.isAfter(enterTime)) {
                        int remainSize = PAGE_SIZE - findByRedisStream.size();
                        log.info("몽고에서 추가 조회해야하는 데이터 수 : {}", remainSize);
                        PageRequest pageRequest = PageRequest.of(0, remainSize + 1,
                            Sort.by(Direction.DESC, "createdAt", "sequence"));

                        Chat lastChat = findByRedisStream.get(findByRedisStream.size() - 1);
                        List<Chat> remainChatList = chatRepositoryService.getChatListByRange(
                            chatRoomId, enterTime, lastChat.getCreatedAt(), lastChat.getSequence(),
                            pageRequest);

                        hasNext = remainChatList.size() > remainSize;
                        log.info("몽고에서 추가 조회된 데이터 수 : {}", remainChatList.size());
                        if (hasNext) {
                            remainChatList = remainChatList.stream()
                                .limit(remainSize)
                                .toList();
                        }

                        chatList.addAll(remainChatList);
                        log.info("몽고에서 추가 데이터 조회 완료");
                    }
                } else {
                    log.info("redis데이터가 끝, 몽고 추가 조회 x");
                }
            }
        }

        return convertToResponse(chatList, hasNext);
    }

    private void produceToStream(CreateChatRequestDTO createChatRequestDTO) {
        Map<String, String> streamContent = new HashMap<>();
        streamContent.put("chatRoomId", String.valueOf(createChatRequestDTO.chatRoomId()));
        streamContent.put("memberId", String.valueOf(createChatRequestDTO.memberId()));
        streamContent.put("content", createChatRequestDTO.content());

        chatStreamService.addStream(
            chatStreamService.generateChatroomStreamKey(createChatRequestDTO.chatRoomId()),
            streamContent);
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

    private List<Chat> findChatListBeforeLastChatTime(Long chatRoomId, LocalDateTime lastChatTime,
        Long sequence, LocalDateTime enterTime) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE + 1,
            Sort.by(Sort.Direction.DESC, "createdAt", "sequence"));

        return chatRepositoryService.getChatListByRange(chatRoomId, enterTime, lastChatTime,
            sequence, pageRequest);
    }

    private ChatListResponseDTO convertToResponse(List<Chat> chatList, boolean hasNext) {
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
