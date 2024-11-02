package com.cozymate.cozymate_server.domain.memberstatequality.service;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityBulkRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.repository.MemberStatEqualityRepository;
import com.cozymate.cozymate_server.domain.memberstatequality.util.MemberStatEqualityUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        List<MemberStat> memberStatList = memberStatRepository.findByMember_GenderAndMember_University_Id(
            newMemberStat.getMember().getGender(),
            newMemberStat.getMember().getUniversity().getId()
        );

        Long newMemberId = newMemberStat.getMember().getId();

        List<MemberStatEquality> memberStatEqualityList = memberStatList.stream()
            //자기 자신을 제외
            .filter(memberStat -> !memberStat.getId().equals(newMemberStat.getId()))
            .flatMap(memberStat -> {

                Integer equality = MemberStatEqualityUtil.calculateEquality(newMemberStat,
                    memberStat);

                Long originalMemberId = memberStat.getMember().getId();

                MemberStatEquality equalityA = MemberStatEquality.builder()
                    .memberAId(newMemberId)
                    .memberBId(originalMemberId)
                    .equality(equality)
                    .build();

                MemberStatEquality equalityB = MemberStatEquality.builder()
                    .memberAId(originalMemberId)
                    .memberBId(newMemberId)
                    .equality(equality)
                    .build();

                return Stream.of(equalityA, equalityB);

            })
            .collect(Collectors.toList());

        memberStatEqualityBulkRepository.saveAll(memberStatEqualityList);

    }

    /**
     * @return : void
     * @action : 없는 일치율 재생성
     */
    public void generateAllMemberStatEquality(
    ) {

        // 12000ms -> 600ms로 개선
        // 모든 MemberStat을 성별과 학교 기준으로 한 번에 가져옴 (Member도 함께 불러오기)
        List<MemberStat> allMemberStats = memberStatRepository.findAllWithMember(); // JOIN FETCH 사용

        // 이미 존재하는 모든 MemberStatEquality를 한 번에 가져와서 Set으로 저장
        Set<String> existingEqualities = memberStatEqualityRepository.findAll().stream()
            .flatMap(equality -> Stream.of(
                equality.getMemberAId() + "-" + equality.getMemberBId(),  // A -> B
                equality.getMemberBId() + "-" + equality.getMemberAId())  // B -> A
            )
            .collect(Collectors.toSet());

        // 성별과 학교를 기준으로 MemberStat을 그룹화
        Map<String, List<MemberStat>> groupedMemberStats = allMemberStats.stream()
            .collect(Collectors.groupingBy(
                memberStat -> memberStat.getMember().getGender() + "-" + memberStat.getMember().getUniversity()
                    .getId()
            ));

        // 일치율 리스트 생성
        List<MemberStatEquality> memberStatEqualityList = new ArrayList<>();

        // 그룹별로 처리
        groupedMemberStats.forEach((key, memberStats) -> {
            for (int i = 0; i < memberStats.size(); i++) {
                MemberStat memberStatA = memberStats.get(i);

                for (int j = i + 1; j < memberStats.size(); j++) {
                    MemberStat memberStatB = memberStats.get(j);

                    // Set을 이용해 A -> B와 B -> A의 존재 여부를 빠르게 확인
                    String keyAtoB =
                        memberStatA.getMember().getId() + "-" + memberStatB.getMember().getId();
                    String keyBtoA =
                        memberStatB.getMember().getId() + "-" + memberStatA.getMember().getId();

                    // A -> B 일치율이 없을 경우
                    if (!existingEqualities.contains(keyAtoB)) {
                        Integer equality = MemberStatEqualityUtil.calculateEquality(memberStatA,
                            memberStatB);

                        MemberStatEquality equalityAtoB = MemberStatEquality.builder()
                            .memberAId(memberStatA.getMember().getId())
                            .memberBId(memberStatB.getMember().getId())
                            .equality(equality)
                            .build();
                        memberStatEqualityList.add(equalityAtoB);

                        // 새로 추가된 A -> B를 Set에 추가하여 중복 방지
                        existingEqualities.add(keyAtoB);
                    }

                    // B -> A 일치율이 없을 경우
                    if (!existingEqualities.contains(keyBtoA)) {
                        Integer equality = MemberStatEqualityUtil.calculateEquality(memberStatA,
                            memberStatB);

                        MemberStatEquality equalityBtoA = MemberStatEquality.builder()
                            .memberAId(memberStatB.getMember().getId())
                            .memberBId(memberStatA.getMember().getId())
                            .equality(equality)
                            .build();
                        memberStatEqualityList.add(equalityBtoA);

                        // 새로 추가된 B -> A를 Set에 추가하여 중복 방지
                        existingEqualities.add(keyBtoA);
                    }
                }
            }
        });

        // 새로운 일치율 데이터를 저장
        memberStatEqualityBulkRepository.saveAll(memberStatEqualityList);
    }

    /**
     * @return : void
     * @action : 모든 기존 일치율을 다시 계산하여 업데이트
     */
    public void recalculateAllMemberStatEquality() {

        // 1. 모든 MemberStat을 미리 한 번에 불러옴 (memberId와 매핑된 Map으로 관리)
        List<MemberStat> allMemberStats = memberStatRepository.findAllWithMember();
        Map<Long, MemberStat> memberStatMap = allMemberStats.stream()
            .collect(Collectors.toMap(ms -> ms.getMember().getId(), ms -> ms));

        List<MemberStatEquality> allEqualities = memberStatEqualityRepository.findAll();

        List<MemberStatEquality> updatedEqualities = new ArrayList<>();

        for (MemberStatEquality equality : allEqualities) {
            MemberStat memberStatA = memberStatMap.get(equality.getMemberAId());
            MemberStat memberStatB = memberStatMap.get(equality.getMemberBId());

            if (memberStatA != null && memberStatB != null) {
                Integer newEqualityValue = MemberStatEqualityUtil.calculateEquality(memberStatA,
                    memberStatB);

                if (!newEqualityValue.equals(equality.getEquality())) {
                    equality.updateEquality(newEqualityValue);
                    updatedEqualities.add(equality); // 업데이트할 리스트에 추가
                }
            }
        }

        if (!updatedEqualities.isEmpty()) {
            memberStatEqualityBulkRepository.updateAll(updatedEqualities); // 배치 저장으로 성능 최적화
        }
    }


    /**
     * @param changedMemberStat : 변경된 멤버의 상세정보
     * @return : void
     */
    public void updateMemberStatEqualities(MemberStat changedMemberStat) {

        Long changedMemberId = changedMemberStat.getMember().getId();

        List<MemberStatEquality> relatedEqualities = memberStatEqualityRepository.findAllByMemberAIdOrMemberBId(
            changedMemberId, changedMemberId);

        Set<Long> memberIds = relatedEqualities.stream()
            .flatMap(equality -> Stream.of(equality.getMemberAId(), equality.getMemberBId()))
            .collect(Collectors.toSet());

        Map<Long, MemberStat> memberStatMap = memberStatRepository.findMemberStatsAndMemberIdsByMemberIds(
                memberIds)
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(1, Long.class),
                tuple -> tuple.get(0, MemberStat.class)
            ));

        List<MemberStatEquality> updatedEqualities = relatedEqualities.stream()
            .map(
                equality -> {
                    boolean isMemberAChanged = equality.getMemberAId().equals(changedMemberId);

                    MemberStat memberStat = isMemberAChanged ?
                        memberStatMap.get(equality.getMemberBId()) :
                        memberStatMap.get(equality.getMemberAId());

                    Integer updatedEquality = MemberStatEqualityUtil.calculateEquality(
                        changedMemberStat, memberStat);

                    return equality.updateEquality(updatedEquality);
                }
            )
            .toList();

        memberStatEqualityBulkRepository.updateAll(updatedEqualities);

    }

    /**
     * @param deletedMemberStat : 삭제할 멤버의 상세정보
     * @return : void
     **/
    public void deleteMemberStatEqualities(MemberStat deletedMemberStat) {

        Long memberId = deletedMemberStat.getMember().getId();

        List<MemberStatEquality> relatedEqualities = memberStatEqualityRepository.findAllByMemberAIdOrMemberBId(
            memberId, memberId);

        memberStatEqualityBulkRepository.deleteAll(relatedEqualities);

    }

    /**
     * @param memberId : 삭제할 멤버의 Id
     * @return : void
     */
    public void deleteMemberStatEqualitiesWithMemberId(Long memberId) {

        List<MemberStatEquality> relatedEqualities = memberStatEqualityRepository.findAllByMemberAIdOrMemberBId(
            memberId, memberId);

        memberStatEqualityBulkRepository.deleteAll(relatedEqualities);

    }

}
