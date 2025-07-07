package com.cozymate.cozymate_server.auth.repository;

import com.cozymate.cozymate_server.auth.Token;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {
    Optional<Token> findByRefreshToken(String refreshToken);
}
