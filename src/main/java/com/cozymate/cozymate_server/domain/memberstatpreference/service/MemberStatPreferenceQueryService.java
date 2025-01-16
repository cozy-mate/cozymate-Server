package com.cozymate.cozymate_server.domain.memberstatpreference.service;

import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import com.cozymate.cozymate_server.domain.memberstatpreference.dto.MemberStatPreferenceDto;
import com.cozymate.cozymate_server.domain.memberstatpreference.repository.MemberStatPreferenceRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Arrays;
import java.util.List;
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

    // 컨트롤러용
    public MemberStatPreferenceDto getPreferences(Long memberId) {
        MemberStatPreference memberStatPreference = memberStatPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS));
        return MemberStatPreferenceDto.builder()
            .preferenceList(
                Arrays.asList(memberStatPreference.getSelectedPreferences().split(","))
            )
            .build();
    }

    // 타 서비스용
    public List<String> getPreferencesToList(Long memberId) {
        MemberStatPreference memberStatPreference = memberStatPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS));

        return Arrays.asList(memberStatPreference.getSelectedPreferences().split(","));
    }

}
