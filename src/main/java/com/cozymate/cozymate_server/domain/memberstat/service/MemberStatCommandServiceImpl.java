package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.entity.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.handler.MemberHandler;
import com.cozymate.cozymate_server.global.response.handler.UniversityHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatCommandServiceImpl implements MemberStatCommandService{

    private final MemberStatRepository memberStatRepository;
    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    @Override
    public Long createMemberStat(Long memberId, MemberStatRequestDTO memberStatRequestDTO) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(()->new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
        University university = universityRepository.findById(memberStatRequestDTO.getUniversityId())
            .orElseThrow(()->new UniversityHandler(ErrorStatus._UNIVERSITY_NOT_FOUND));

        MemberStat saveMemberStat = memberStatRepository.save(
            MemberStat.builder()
                .member(member)
                .university(university)
                .admissionYear(memberStatRequestDTO.getAdmissionYear())
                .major(memberStatRequestDTO.getMajor())
                .numOfRoommate(memberStatRequestDTO.getNumOfRoommate())
                .acceptance(memberStatRequestDTO.getAcceptance())
                .wakeUpTime(memberStatRequestDTO.getWakeUpTime())
                .sleepingTime(memberStatRequestDTO.getSleepingTime())
                .turnOffTime(memberStatRequestDTO.getTurnOffTime())
                .smoking(memberStatRequestDTO.getSmokingState())
                .sleepingHabit(memberStatRequestDTO.getSleepingHabit())
                .constitution(memberStatRequestDTO.getConstitution())
                .lifePattern(memberStatRequestDTO.getLifePattern())
                .intimacy(memberStatRequestDTO.getIntimacy())
                .canShare(memberStatRequestDTO.getCanShare())
                .isPlayGame(memberStatRequestDTO.getIsPlayGame())
                .isPhoneCall(memberStatRequestDTO.getIsPhoneCall())
                .studying(memberStatRequestDTO.getStudying())
                .cleanSensitivity(memberStatRequestDTO.getCleanSensitivity())
                .noiseSensitivity(memberStatRequestDTO.getNoiseSensitivity())
                .cleaningFrequency(memberStatRequestDTO.getCleaningFrequency())
                .personality(memberStatRequestDTO.getPersonality())
                .mbti(memberStatRequestDTO.getMbti())
                .options(memberStatRequestDTO.getOptions())
                .build());

        return saveMemberStat.getId();
    }
}
