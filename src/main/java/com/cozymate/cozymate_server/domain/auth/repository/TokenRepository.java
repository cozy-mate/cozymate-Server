package com.cozymate.cozymate_server.domain.auth.repository;

import com.cozymate.cozymate_server.domain.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {



}
