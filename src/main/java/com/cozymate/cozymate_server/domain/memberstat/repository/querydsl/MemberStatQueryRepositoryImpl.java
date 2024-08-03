package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import static com.cozymate.cozymate_server.domain.memberstat.QMemberStat.memberStat;

@RequiredArgsConstructor
@Repository
public class MemberStatQueryRepositoryImpl implements
    MemberStatQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberStat> getFilteredMemberStat(List<String> filterList,
        MemberStat criteriaMemberStat) {

        JPAQuery<MemberStat> baseQuery = queryFactory.selectFrom(memberStat);
        BooleanBuilder builder = new BooleanBuilder();
        HashMap<String, BooleanExpression> booleanExpressionHashMap = new HashMap<String, BooleanExpression>(
            bindStringToBooleanExpression(criteriaMemberStat));

        filterList.forEach(
            (value) -> {
                if (!booleanExpressionHashMap.containsKey(value)) {
                    throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
                }
                builder.and(booleanExpressionHashMap.get(value));
            }
        );
        baseQuery.where(builder);
        List<MemberStat> result = baseQuery.fetch();

        return result;
    }

    private BooleanExpression acceptanceEq(final String acceptance) {
        return acceptance == null ? null : memberStat.acceptance.eq(acceptance);
    }

    private BooleanExpression admissionYearEq(final Integer admissionYear) {
        return admissionYear == null ? null : memberStat.admissionYear.eq(admissionYear);
    }

    private BooleanExpression majorEq(final String major) {
        return major == null ? null : memberStat.major.eq(major);
    }

    private BooleanExpression numOfRoommateEq(final Integer numOfRoommate) {
        return numOfRoommate == null ? null : memberStat.numOfRoommate.eq(numOfRoommate);
    }

    private BooleanExpression wakeUpTimeEq(final Integer wakeUpTime) {
        return wakeUpTime == null ? null : memberStat.wakeUpTime.eq(wakeUpTime);
    }

    private BooleanExpression sleepingTimeEq(final Integer sleepingTime) {
        return sleepingTime == null ? null : memberStat.sleepingTime.eq(sleepingTime);
    }

    private BooleanExpression turnOffTimeEq(final Integer turnOffTime) {
        return turnOffTime == null ? null : memberStat.turnOffTime.eq(turnOffTime);
    }

    private BooleanExpression smokingEq(final String smoking) {
        return smoking == null ? null : memberStat.smoking.eq(smoking);
    }

    private BooleanExpression sleepingHabitEq(final String sleepingHabit) {
        return sleepingHabit == null ? null : memberStat.sleepingHabit.eq(sleepingHabit);
    }

    private BooleanExpression airConditioningIntensityEq(final Integer airConditioningIntensity) {
        return airConditioningIntensity == null ? null : memberStat.airConditioningIntensity.eq(airConditioningIntensity);
    }

    private BooleanExpression heatingIntensityEq(final Integer heatingIntensity) {
        return heatingIntensity == null ? null : memberStat.heatingIntensity.eq(heatingIntensity);
    }

    private BooleanExpression lifePatternEq(final String lifePattern) {
        return lifePattern == null ? null : memberStat.lifePattern.eq(lifePattern);
    }

    private BooleanExpression intimacyEq(final String intimacy) {
        return intimacy == null ? null : memberStat.intimacy.eq(intimacy);
    }

    private BooleanExpression canShareEq(final Boolean canShare) {
        return canShare == null ? null : memberStat.canShare.eq(canShare);
    }

    private BooleanExpression isPlayGameEq(final Boolean isPlayGame) {
        return isPlayGame == null ? null : memberStat.isPlayGame.eq(isPlayGame);
    }

    private BooleanExpression isPhoneCallEq(final Boolean isPhoneCall) {
        return isPhoneCall == null ? null : memberStat.isPhoneCall.eq(isPhoneCall);
    }

    private BooleanExpression studyingEq(final String studying) {
        return studying == null ? null : memberStat.studying.eq(studying);
    }

    private BooleanExpression cleanSensitivityEq(final Integer cleanSensitivity) {
        return cleanSensitivity == null ? null : memberStat.cleanSensitivity.eq(cleanSensitivity);
    }

    private BooleanExpression noiseSensitivityEq(final Integer noiseSensitivity) {
        return noiseSensitivity == null ? null : memberStat.noiseSensitivity.eq(noiseSensitivity);
    }

    private BooleanExpression cleaningFrequencyEq(final String cleaningFrequency) {
        return cleaningFrequency == null ? null :memberStat.cleaningFrequency.eq(cleaningFrequency);
    }

    private BooleanExpression personalityEq(final String personality) {
        return personality == null ? null : memberStat.personality.eq(personality);
    }

    private BooleanExpression mbtiEq(final String mbti) {
        return mbti == null ? null : memberStat.mbti.eq(mbti);
    }

    private Map<String, BooleanExpression> bindStringToBooleanExpression(
        MemberStat criteriaMemberStat) {
        Map<String, BooleanExpression> fieldMap = new HashMap<>();
        fieldMap.put("acceptance", acceptanceEq(criteriaMemberStat.getAcceptance()));
        fieldMap.put("admissionYear", admissionYearEq(criteriaMemberStat.getAdmissionYear()));
        fieldMap.put("major", majorEq(criteriaMemberStat.getMajor()));
        fieldMap.put("numOfRoommate", numOfRoommateEq(criteriaMemberStat.getNumOfRoommate()));
        fieldMap.put("wakeUpTime", wakeUpTimeEq(criteriaMemberStat.getWakeUpTime()));
        fieldMap.put("sleepingTime", sleepingTimeEq(criteriaMemberStat.getSleepingTime()));
        fieldMap.put("turnOffTime", turnOffTimeEq(criteriaMemberStat.getTurnOffTime()));
        fieldMap.put("smoking", smokingEq(criteriaMemberStat.getSmoking()));
        fieldMap.put("sleepingHabit", sleepingHabitEq(criteriaMemberStat.getSleepingHabit()));
        fieldMap.put("airConditioningIntensity",
            airConditioningIntensityEq(criteriaMemberStat.getAirConditioningIntensity()));
        fieldMap.put("heatingIntensity",
            heatingIntensityEq(criteriaMemberStat.getHeatingIntensity()));
        fieldMap.put("lifePattern", lifePatternEq(criteriaMemberStat.getLifePattern()));
        fieldMap.put("intimacy", intimacyEq(criteriaMemberStat.getIntimacy()));
        fieldMap.put("canShare", canShareEq(criteriaMemberStat.getCanShare()));
        fieldMap.put("isPlayGame", isPlayGameEq(criteriaMemberStat.getIsPlayGame()));
        fieldMap.put("isPhoneCall", isPhoneCallEq(criteriaMemberStat.getIsPhoneCall()));
        fieldMap.put("studying", studyingEq(criteriaMemberStat.getStudying()));
        fieldMap.put("cleanSensitivity",
            cleanSensitivityEq(criteriaMemberStat.getCleanSensitivity()));
        fieldMap.put("noiseSensitivity",
            noiseSensitivityEq(criteriaMemberStat.getNoiseSensitivity()));
        fieldMap.put("cleaningFrequency",
            cleanSensitivityEq(criteriaMemberStat.getCleanSensitivity()));
        fieldMap.put("personality", personalityEq(criteriaMemberStat.getPersonality()));
        fieldMap.put("mbti", mbtiEq(criteriaMemberStat.getMbti()));
        return fieldMap;
    }

}
