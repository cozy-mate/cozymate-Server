package com.cozymate.cozymate_server.domain.mail.repository;

import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAuthenticationRepositoryService {

    private final MailAuthenticationRepository mailAuthenticationRepository;

    public MailAuthentication getMailAuthenticationByIdOrThrow(String clientId) {
        return mailAuthenticationRepository.findById(clientId)
            .orElseThrow(() -> new GeneralException(
                ErrorStatus._MAIL_AUTHENTICATION_NOT_FOUND)
            );
    }

    public Optional<MailAuthentication> getMailAuthenticationByIdOptional(String clientId){
        return mailAuthenticationRepository.findById(clientId);
    }

    public MailAuthentication createMailAuthentication(MailAuthentication mailAuthentication){
        return mailAuthenticationRepository.save(mailAuthentication);
    }

    public List<MailAuthentication> getMailAuthenticationListByMailAddress(String mailAddress){
        return mailAuthenticationRepository.findAllByMailAddress(mailAddress);
    }
}
