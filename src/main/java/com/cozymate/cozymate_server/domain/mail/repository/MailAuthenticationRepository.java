package com.cozymate.cozymate_server.domain.mail.repository;

import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailAuthenticationRepository extends JpaRepository<MailAuthentication, String> {
    Optional<MailAuthentication> findByMailAddress(String mailAddress);

    List<MailAuthentication> findAllByMailAddress(String mailAddress);
}
