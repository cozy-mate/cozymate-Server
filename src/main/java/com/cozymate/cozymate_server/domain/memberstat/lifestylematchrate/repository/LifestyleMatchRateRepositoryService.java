package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate.LifestyleMatchRateId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LifestyleMatchRateRepositoryService {

    private final LifestyleMatchRateRepository repository;
    private final LifestyleMatchRateBulkRepository bulkRepository;

    public LifestyleMatchRate getLifestyleMatchRateByIdOrNoMatchRate(
        LifestyleMatchRateId lifestyleMatchRateId) {
        return repository.findById(
            lifestyleMatchRateId).orElse(null);
    }

    public Optional<LifestyleMatchRate> getLifestyleMatchRateByIdOptional(
        LifestyleMatchRateId lifestyleMatchRateId) {
        return repository.findById(lifestyleMatchRateId);
    }


    public List<LifestyleMatchRate> getLifestyleMatchRateListByIdList(
        List<LifestyleMatchRateId> lifestyleMatchRateIdList) {
        return repository.findByIdList(lifestyleMatchRateIdList);
    }

    public List<LifestyleMatchRate> getLifestyleMatchRateListBySingleMemberId(Long memberId) {
        return repository.findBySingleMemberId(memberId);
    }

    @Transactional
    public void createLifestyleMatchRate(LifestyleMatchRate lifestyleMatchRate){
        repository.save(lifestyleMatchRate);
    }

    @Transactional
    public void createAndUpdateLifestyleMatchRateList(List<LifestyleMatchRate> lifestyleMatchRateList){
        bulkRepository.saveAllWithUpsert(lifestyleMatchRateList);
    }
}
