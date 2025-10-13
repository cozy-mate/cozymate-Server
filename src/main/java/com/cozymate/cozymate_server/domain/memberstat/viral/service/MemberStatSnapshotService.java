package com.cozymate.cozymate_server.domain.memberstat.viral.service;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.util.MemberMatchRateCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.FieldInstanceResolver;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.MemberStatComparator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import com.cozymate.cozymate_server.domain.memberstat.viral.MemberStatSnapshot;
import com.cozymate.cozymate_server.domain.memberstat.viral.converter.MemberStatSnapshotConverter;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateViralSnapshotDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateMemberStatSnapshotRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.repository.MemberStatSnapshotRepository;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberStatSnapshotService {

    private final MemberStatSnapshotRepository repository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Long getNumberOfViralSnapshots(){
        return repository.count();
    }
    @Transactional
    public CreateViralSnapshotDTO create(CreateMemberStatSnapshotRequestDTO dto) {
        MemberStatSnapshot snapshot = createSnapshot(dto);
        return createEmptyListAndOnlyCode(snapshot.getViralCode());
    }

    @Transactional
    public CreateViralSnapshotDTO createAndCompare(CreateMemberStatSnapshotRequestDTO dto,
        String sharerViralCode) {
        MemberStatSnapshot sharer;
        MemberStatSnapshot criteria = createSnapshot(dto);

        try {
            sharer = loadSharerSnapshot(sharerViralCode);
        } catch (GeneralException e) {
            return createEmptyListAndOnlyCode(criteria.getViralCode());
        }

        Map<String, String> sharerMap = toStringMap(
            FieldInstanceResolver.extractAllLifestyleFields(sharer.getLifestyle()));
        Map<String, String> criteriaMap = toStringMap(
            FieldInstanceResolver.extractAllLifestyleFields(criteria.getLifestyle()));
        Integer matchRate = MemberMatchRateCalculator.calculateLifestyleMatchRate(
            sharer.getLifestyle(),criteria.getLifestyle()
        );

        ComparisonResult result = compareMaps(sharerMap, criteriaMap, matchRate);

        return buildCompareDto(criteria.getViralCode(), result);
    }

    private CreateViralSnapshotDTO createEmptyListAndOnlyCode(String viralCode) {
        return buildCompareDto(
            viralCode,
            createEmptyResult()
        );
    }

    private MemberStatSnapshot loadSharerSnapshot(String sharerViralCode) {
        MemberStatSnapshot sharer = repository.findByViralCode(sharerViralCode);
        if (sharer == null) {
            throw new GeneralException(ErrorStatus._VIRAL_CODE_NOT_FOUND);
        }
        return sharer;
    }

    private MemberStatSnapshot createSnapshot(CreateMemberStatSnapshotRequestDTO dto) {
        MemberStatSnapshot entity = MemberStatSnapshotConverter.toEntity(dto);
        MemberStatSnapshot saved = repository.save(entity);
        entityManager.flush();
        entityManager.refresh(saved);
        return saved;
    }

    private Map<String, String> toStringMap(Map<String, Object> raw) {
        return QuestionAnswerMapper.convertToStringMap(raw);
    }

    /**
     * 두 맵을 기준으로 SAME/DIFFERENT/AMBIGUOUS 분류
     */
    private ComparisonResult compareMaps(
        Map<String, String> left, Map<String, String> right, Integer matchRate) {
        Set<String> keys = new HashSet<>();
        keys.addAll(left.keySet());
        keys.addAll(right.keySet());

        List<String> same = new ArrayList<>();
        List<String> different = new ArrayList<>();
        List<String> ambiguous = new ArrayList<>();

        for (String key : keys) {
            DifferenceStatus s = MemberStatComparator.compareField(left.get(key), right.get(key));
            switch (s) {
                case SAME -> same.add(key);
                case DIFFERENT -> different.add(key);
                case NOT_SAME_NOT_DIFFERENT -> ambiguous.add(key);
            }
        }
        return new ComparisonResult(matchRate, same, different, ambiguous);
    }

    private CreateViralSnapshotDTO buildCompareDto(String viralCode, ComparisonResult result) {
        return CreateViralSnapshotDTO.builder()
            .viralCode(viralCode)
            .matchRate(result.matchRate)
            .sameValues(result.same)
            .differentValues(result.different)
            .ambiguousValues(result.ambiguous)
            .build();
    }

    private ComparisonResult createEmptyResult() {
        return new ComparisonResult(
            0,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    private record ComparisonResult(
        Integer matchRate,
        List<String> same,
        List<String> different,
        List<String> ambiguous
    ) {

    }

}
