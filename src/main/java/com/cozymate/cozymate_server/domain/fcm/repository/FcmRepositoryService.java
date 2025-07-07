package com.cozymate.cozymate_server.domain.fcm.repository;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmRepositoryService {

    private final FcmRepository fcmRepository;

    public Optional<Fcm> getFcmOptionalByClientId(String clientId) {
        return fcmRepository.findById(clientId);
    }

    public void createFcm(Fcm fcm) {
        fcmRepository.save(fcm);
    }

    public void updateFcmValidToFalseByToken(String token) {
        fcmRepository.updateValidByToken(token);
    }

    public void deleteFcmByMemberId(Long memberId) {
        fcmRepository.deleteAllByMemberId(memberId);
    }

    public void updateFcmValidToFalseByTokenList(List<String> tokenList) {
        fcmRepository.updateValidByTokenList(tokenList);
    }
}
