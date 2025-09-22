package com.cozymate.cozymate_server.domain.message.repository;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findTopByMessageRoomOrderByIdDesc(MessageRoom messageRoom);

    @Modifying
    @Query("delete from Message m where m.messageRoom = :messageRoom")
    void deleteAllByMessageRoom(@Param("messageRoom") MessageRoom messageRoom);

    @Query("select m from Message m where m.messageRoom = :messageRoom")
    Slice<Message> findAllByMessageRoom(@Param("messageRoom") MessageRoom messageRoom, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.sender = null WHERE m.sender = :member")
    void bulkDeleteSender(@Param("member") Member member);

    @Query("select case when count(m) > 0 then true else false end " +
        "from Message m " +
        "where m.sender = :member and m.messageRoom = :messageRoom " +
        "and (m.createdAt > :lastSeenAt or :lastSeenAt is null)")
    boolean existsBySenderAndMessageRoomAndCreatedAtAfterOrLastSeenAtIsNull(
        @Param("member") Member member, @Param("messageRoom") MessageRoom messageRoom,
        @Param("lastSeenAt") LocalDateTime lastSeenAt);

    @Query("select m from Message m where m.messageRoom = :messageRoom "
        + "and m.createdAt > :lastDeleteAt")
    Slice<Message> findPagingByChatRoomAndLastDeleteAt(@Param("messageRoom") MessageRoom messageRoom,
        LocalDateTime lastDeleteAt, Pageable pageable);
}