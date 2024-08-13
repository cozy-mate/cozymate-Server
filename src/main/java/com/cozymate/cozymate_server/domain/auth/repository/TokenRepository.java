package com.cozymate.cozymate_server.domain.auth.repository;

import com.cozymate.cozymate_server.domain.auth.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByRefreshToken(String refreshTokenValue);
}
