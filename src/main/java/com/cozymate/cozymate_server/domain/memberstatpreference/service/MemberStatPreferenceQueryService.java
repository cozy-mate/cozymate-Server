package com.cozymate.cozymate_server.domain.memberstatpreference.service;

import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import com.cozymate.cozymate_server.domain.memberstatpreference.dto.MemberStatPreferenceDto;
import com.cozymate.cozymate_server.domain.memberstatpreference.repository.MemberStatPreferenceRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberStatPreferenceQueryService {

    private final MemberStatPreferenceRepository memberStatPreferenceRepository;

    public MemberStatPreferenceDto getPreferences(Long memberId) {
        MemberStatPreference memberStatPreference = memberStatPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS));
        return MemberStatPreferenceDto.builder()
            .preferences(
                Arrays.asList(memberStatPreference.getSelectedPreferences().split(","))
            )
            .build();
    }

}
