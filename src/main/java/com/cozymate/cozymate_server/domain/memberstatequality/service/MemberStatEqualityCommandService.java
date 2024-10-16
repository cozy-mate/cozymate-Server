package com.cozymate.cozymate_server.domain.memberstatequality.service;

import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityBulkRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.util.MemberStatEqualityUtil;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberStatEqualityCommandService {

    private final MemberStatEqualityRepository memberStatEqualityRepository;
    private final MemberStatEqualityBulkRepository memberStatEqualityBulkRepository;
    private final MemberStatRepository memberStatRepository;

    /**
     * @param newMemberStat : 새롭게 추가된 멤버 상세정보
     * @return : void
     */
    public void createMemberStatEqualities(MemberStat newMemberStat) {

        // 성별이 같고, 같은 학교일 경우에 대해서만 일치율 계산
        List<MemberStat> memberStatList = memberStatRepository.findByMember_GenderAndUniversity_Id(
            newMemberStat.getMember().getGender(),
            newMemberStat.getUniversity().getId()
        );

        List<MemberStatEquality> memberStatEqualityList = memberStatList.stream()
            //자기 자신을 제외
            .filter(memberStat -> !memberStat.getId().equals(newMemberStat.getId()))
            .flatMap(memberStat -> {

                Integer equality = MemberStatEqualityUtil.calculateEquality(newMemberStat,
                    memberStat);

                MemberStatEquality equalityA = MemberStatEquality.builder()
                    .memberAId(newMemberStat.getMember().getId())
                    .memberBId(memberStat.getMember().getId())
                    .equality(equality)
                    .build();

                MemberStatEquality equalityB = MemberStatEquality.builder()
                    .memberAId(memberStat.getMember().getId())
                    .memberBId(newMemberStat.getMember().getId())
                    .equality(equality)
                    .build();

                return Stream.of(equalityA, equalityB);

            })
            .collect(Collectors.toList());

        memberStatEqualityBulkRepository.saveAll(memberStatEqualityList);

    }

    public void generateAllMemberStatEquality() {

        long startTime = System.currentTimeMillis();

        // 모든 MemberStat을 성별과 학교 기준으로 필터링하여 가져옴
        List<MemberStat> allMemberStats = memberStatRepository.findAll();

        // 각 MemberStat에 대해 성별과 학교가 같은 다른 MemberStat 목록 가져오기
        List<MemberStatEquality> memberStatEqualityList = allMemberStats.stream()
            .flatMap(memberStatA -> {
                // 성별과 학교가 같은 다른 MemberStat들을 가져옴
                List<MemberStat> filteredMemberStats = memberStatRepository.findByMember_GenderAndUniversity_Id(
                    memberStatA.getMember().getGender(),
                    memberStatA.getUniversity().getId()
                );

                return filteredMemberStats.stream()
                    .filter(memberStatB -> !memberStatA.getId()
                        .equals(memberStatB.getId())) // 자신과의 비교 제외
                    // 중복되는 일치율 제외
                    .filter(
                        memberStatB -> !memberStatEqualityRepository.existsByMemberAIdAndMemberBId(
                            memberStatA.getMember().getId(), memberStatB.getMember().getId()))
                    .flatMap(memberStatB -> {
                        Integer equality = MemberStatEqualityUtil.calculateEquality(memberStatA,
                            memberStatB);

                        MemberStatEquality equalityA = MemberStatEquality.builder()
                            .memberAId(memberStatA.getMember().getId())
                            .memberBId(memberStatB.getMember().getId())
                            .equality(equality)
                            .build();

                        MemberStatEquality equalityB = MemberStatEquality.builder()
                            .memberAId(memberStatB.getMember().getId())
                            .memberBId(memberStatA.getMember().getId())
                            .equality(equality)
                            .build();

                        return Stream.of(equalityA, equalityB);
                    });
            })
            .collect(Collectors.toList());

        // 일치율 계산 시간 출력
        System.out.println("Calculation time = " + (System.currentTimeMillis() - startTime) + "ms");

        // 새로운 일치율 데이터를 저장
        memberStatEqualityBulkRepository.saveAll(memberStatEqualityList);

        // 저장 시간 출력
        System.out.println("Save time = " + (System.currentTimeMillis() - startTime) + "ms");
    }


    /**
     * @param changedMemberStat : 변경된 멤버의 상세정보
     * @return : void
     */

    public void updateMemberStatEqualities(MemberStat changedMemberStat) {

        List<MemberStatEquality> relatedEqualities = memberStatEqualityRepository.findAllByMemberAIdOrMemberBId(
            changedMemberStat.getId(), changedMemberStat.getId());

        // 필요 없는 코드 고치기
        List<MemberStatEquality> updatedEqualities = relatedEqualities.stream()
            .peek(equality -> {

                MemberStat memberA = memberStatRepository.findByMemberId(equality.getMemberAId())
                    .orElseThrow(
                        () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
                    );

                MemberStat memberB = memberStatRepository.findByMemberId(equality.getMemberBId())
                    .orElseThrow(
                        () -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS)
                    );

                Integer newEqualityScore = MemberStatEqualityUtil.calculateEquality(memberA,
                    memberB);

                equality.updateEquality(
                    newEqualityScore
                );

            })
            .toList();

    }

    /**
     * @param deletedMemberStat : 삭제할 멤버의 상세정보
     * @return : void
     */
    public void deleteMemberStatEqualities(MemberStat deletedMemberStat) {

        List<MemberStatEquality> relatedEqualities = memberStatEqualityRepository.findAllByMemberAIdOrMemberBId(
            deletedMemberStat.getId(), deletedMemberStat.getId());

        memberStatEqualityRepository.deleteAll(relatedEqualities);

    }
}
