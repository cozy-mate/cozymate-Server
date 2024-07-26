package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatCommandService {

    private final MemberStatRepository memberStatRepository;
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;

    private static final String MORNING = "오전";
    private static final String AFTERNOON = "오후";
    private static final Integer HALFDAY = 12;

    public Long createMemberStat(Long memberId, MemberStatRequestDTO memberStatRequestDTO) {

        if (memberStatRepository.findByMemberId(memberId).isPresent()) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_EXISTS);
        }

        Member member = memberRepository.findById(memberId)
            .orElseThrow(()->new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        University university = universityRepository.findById(memberStatRequestDTO.getUniversityId())
            .orElseThrow(()->new GeneralException(ErrorStatus._UNIVERSITY_NOT_FOUND));

        Integer admissionYear = Integer.parseInt(memberStatRequestDTO.getAdmissionYear());

        Integer wakeUpTime = convertTime(memberStatRequestDTO.getWakeUpMeridian(), memberStatRequestDTO.getWakeUpTime());
        Integer sleepingTime = convertTime(memberStatRequestDTO.getSleepingMeridian(), memberStatRequestDTO.getSleepingTime());
        Integer turnOffTime = convertTime(memberStatRequestDTO.getTurnOffMeridian(), memberStatRequestDTO.getTurnOffTime());


        MemberStat saveMemberStat = memberStatRepository.save(
            MemberStatConverter.toEntity(
                member, university, admissionYear, wakeUpTime, sleepingTime, turnOffTime, memberStatRequestDTO)
            );

        return saveMemberStat.getId();
    }

    private Integer convertTime(String median, Integer time){
        switch (median){
            case MORNING:
                if (!time.equals(HALFDAY)){
                    return time;
                }else{
                    return 0;
                }
            case AFTERNOON:
                if(time.equals(HALFDAY)){
                    return time;
                }else {
                    return time + HALFDAY;
                }
            default:
                throw new GeneralException(ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID);
        }
    }
}
