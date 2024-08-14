package com.cozymate.cozymate_server.domain.fcm.service;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.fcm.converter.FcmConverter;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmRequestDto;
import com.cozymate.cozymate_server.domain.fcm.repository.FcmRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FcmCommandService {

    private final FcmRepository fcmRepository;

    public void createFcm(Member member, FcmRequestDto fcmRequestDto) {
        fcmRepository.findById(fcmRequestDto.getDeviceId())
            .ifPresentOrElse(
                fcm -> fcm.updateToken(fcmRequestDto.getToken()),
                () -> {
                    Fcm fcm = FcmConverter.toFcm(member, fcmRequestDto);
                    fcmRepository.save(fcm);
                }
            );
    }
}