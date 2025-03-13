package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.converter.FcmConverter;
import com.cozymate.cozymate_server.domain.fcm.dto.request.FcmRequestDTO;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FcmCommandService {

    private final FcmRepositoryService fcmRepositoryService;

    public void createFcm(Member member, FcmRequestDTO fcmRequestDTO) {
        fcmRepositoryService.getFcmOptionalByClientId(member.getClientId())
                .ifPresentOrElse(
                    fcm -> fcm.updateToken(fcmRequestDTO.token()),
                    () -> {
                        Fcm fcm = FcmConverter.toEntity(member, fcmRequestDTO);
                        fcmRepositoryService.createFcm(fcm);
                    }
                );
    }
}