package com.cozymate.cozymate_server.domain.mail.repository;

import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailAuthentication, String> {
    Optional<MailAuthentication> findByMailAddress(String mailAddress);
}
