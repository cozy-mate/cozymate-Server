package com.cozymate.cozymate_server.domain.friend;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Friend> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Optional<Friend> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(Long sender_id,
        Long receiver_id, Long receiver_id2, Long sender_id2);

    List<Friend> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
