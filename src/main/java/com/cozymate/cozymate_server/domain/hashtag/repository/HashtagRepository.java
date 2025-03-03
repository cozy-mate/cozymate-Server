package com.cozymate.cozymate_server.domain.hashtag.repository;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String name);

}