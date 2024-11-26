package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    void deleteAllByChatRoom(ChatRoom chatRoom);

    @Query("select c from Chat c where c.chatRoom = :chatRoom")
    List<Chat> findAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Query("select case when count(c) > 0 then true else false end " +
        "from Chat c " +
        "where c.sender = :member and c.chatRoom = :chatRoom " +
        "and (c.createdAt > :lastSeenAt or :lastSeenAt is null)")
    boolean existsBySenderAndChatRoomAndCreatedAtAfterOrLastSeenAtIsNull(
        @Param("member") Member member, @Param("chatRoom") ChatRoom chatRoom,
        @Param("lastSeenAt") LocalDateTime lastSeenAt);
}