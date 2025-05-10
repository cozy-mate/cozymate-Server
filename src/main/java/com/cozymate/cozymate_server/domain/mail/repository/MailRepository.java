package com.cozymate.cozymate_server.domain.mail.repository;

import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailAuthentication, String> {
    List<MailAuthentication> findAllByMailAddress(String mailAddress);
}
