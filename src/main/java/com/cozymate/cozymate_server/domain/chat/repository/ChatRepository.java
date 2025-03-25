package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    @Modifying
    @Query("delete from Chat c where c.chatRoom = :chatRoom")
    void deleteAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Query("select c from Chat c where c.chatRoom = :chatRoom")
    Slice<Chat> findAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Modifying
    @Query("UPDATE Chat c SET c.sender = null WHERE c.sender = :member")
    void bulkDeleteSender(@Param("member") Member member);

    @Query("select case when count(c) > 0 then true else false end " +
        "from Chat c " +
        "where c.sender = :member and c.chatRoom = :chatRoom " +
        "and (c.createdAt > :lastSeenAt or :lastSeenAt is null)")
    boolean existsBySenderAndChatRoomAndCreatedAtAfterOrLastSeenAtIsNull(
        @Param("member") Member member, @Param("chatRoom") ChatRoom chatRoom,
        @Param("lastSeenAt") LocalDateTime lastSeenAt);

    @Query("select c from Chat c where c.chatRoom = :chatRoom "
        + "and c.createdAt > :lastDeleteAt")
    Slice<Chat> findByChatRoomAndLastDeleteAt(@Param("chatRoom") ChatRoom chatRoom,
        LocalDateTime lastDeleteAt, Pageable pageable);
}