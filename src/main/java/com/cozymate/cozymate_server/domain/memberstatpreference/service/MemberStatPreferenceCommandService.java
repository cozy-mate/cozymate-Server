package com.cozymate.cozymate_server.domain.memberstatpreference.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import com.cozymate.cozymate_server.domain.memberstatpreference.repository.MemberStatPreferenceRepository;
import com.cozymate.cozymate_server.domain.memberstatpreference.util.MemberStatPreferenceUtil;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatPreferenceCommandService {
    private final MemberStatPreferenceRepository memberStatPreferenceRepository;
    private final MemberRepository memberRepository;

    public Long savePreferences(Long memberId, List<String> selectedPreferences) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if(!MemberStatPreferenceUtil.areValidPreferences(selectedPreferences)){
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID);
        }

        StringJoiner joiner = new StringJoiner(",");
        selectedPreferences.forEach(joiner::add);
        String preferencesString = joiner.toString();


        MemberStatPreference memberStatPreference = MemberStatPreference.builder()
            .member(member)
            .selectedPreferences(preferencesString)
            .build();

        memberStatPreferenceRepository.save(memberStatPreference);

        return memberStatPreference.getId();
    }


    public Long updatePreferences(Long memberId, List<String> newSelectedPreferences) {

        MemberStatPreference memberStatPreference = memberStatPreferenceRepository.findByMemberId(memberId)
            .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS));

        if(!MemberStatPreferenceUtil.areValidPreferences(newSelectedPreferences)){
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID);
        }

        StringJoiner joiner = new StringJoiner(",");
        newSelectedPreferences.forEach(joiner::add);
        String preferencesString = joiner.toString();

        memberStatPreference.update(preferencesString);

        return memberStatPreference.getId();
    }
}
