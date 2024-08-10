package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.cozymate.cozymate_server.domain.memberstat.QMemberStat.memberStat;
import static com.cozymate.cozymate_server.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class MemberStatQueryRepositoryImpl implements
    MemberStatQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberStat> getFilteredMemberStat(List<String> filterList, MemberStat criteriaMemberStat) {
        JPAQuery<MemberStat> baseQuery = queryFactory.selectFrom(memberStat);

        BooleanBuilder builder = new BooleanBuilder();

        //criteriaMemberStat은 제외하기
        builder.and(memberStat.id.ne(criteriaMemberStat.getId()));
        //성별이 틀리면 제외
        builder.and(member.gender.eq(criteriaMemberStat.getMember().getGender()));
        // 대학이 틀리면 제외
        builder.and(memberStat.university.id.eq(criteriaMemberStat.getUniversity().getId()));

        if (filterList != null) {
            filterList.forEach(filter -> applyFilter(builder, filter, criteriaMemberStat));
        }

        return baseQuery
            .join(memberStat.member, member)
            .where(builder)
            .fetch();
    }

    private void applyFilter(BooleanBuilder builder, String filter, MemberStat criteriaMemberStat) {
        switch (filter) {
            case "acceptance":
                builder.and(stringEquals(criteriaMemberStat.getAcceptance(), memberStat.acceptance));
                break;
            case "admissionYear":
                builder.and(integerEquals(criteriaMemberStat.getAdmissionYear(), memberStat.admissionYear));
                break;
            case "major":
                builder.and(stringEquals(criteriaMemberStat.getMajor(), memberStat.major));
                break;
            case "numOfRoommate":
                builder.and(integerEquals(criteriaMemberStat.getNumOfRoommate(), memberStat.numOfRoommate));
                break;
            case "wakeUpTime":
                builder.and(integerEquals(criteriaMemberStat.getWakeUpTime(), memberStat.wakeUpTime));
                break;
            case "sleepingTime":
                builder.and(integerEquals(criteriaMemberStat.getSleepingTime(), memberStat.sleepingTime));
                break;
            case "turnOffTime":
                builder.and(integerEquals(criteriaMemberStat.getTurnOffTime(), memberStat.turnOffTime));
                break;
            case "smoking":
                builder.and(stringEquals(criteriaMemberStat.getSmoking(), memberStat.smoking));
                break;
            case "sleepingHabit":
                builder.and(stringEquals(criteriaMemberStat.getSleepingHabit(), memberStat.sleepingHabit));
                break;
            case "airConditioningIntensity":
                builder.and(integerEquals(criteriaMemberStat.getAirConditioningIntensity(), memberStat.airConditioningIntensity));
                break;
            case "heatingIntensity":
                builder.and(integerEquals(criteriaMemberStat.getHeatingIntensity(), memberStat.heatingIntensity));
                break;
            case "lifePattern":
                builder.and(stringEquals(criteriaMemberStat.getLifePattern(), memberStat.lifePattern));
                break;
            case "intimacy":
                builder.and(stringEquals(criteriaMemberStat.getIntimacy(), memberStat.intimacy));
                break;
            case "canShare":
                builder.and(booleanEquals(criteriaMemberStat.getCanShare(), memberStat.canShare));
                break;
            case "isPlayGame":
                builder.and(booleanEquals(criteriaMemberStat.getIsPlayGame(), memberStat.isPlayGame));
                break;
            case "isPhoneCall":
                builder.and(booleanEquals(criteriaMemberStat.getIsPhoneCall(), memberStat.isPhoneCall));
                break;
            case "studying":
                builder.and(stringEquals(criteriaMemberStat.getStudying(), memberStat.studying));
                break;
            case "intake":
                builder.and(stringEquals(criteriaMemberStat.getIntake(), memberStat.intake));
                break;
            case "cleanSensitivity":
                builder.and(integerEquals(criteriaMemberStat.getCleanSensitivity(), memberStat.cleanSensitivity));
                break;
            case "noiseSensitivity":
                builder.and(integerEquals(criteriaMemberStat.getNoiseSensitivity(), memberStat.noiseSensitivity));
                break;
            case "cleaningFrequency":
                builder.and(stringEquals(criteriaMemberStat.getCleaningFrequency(), memberStat.cleaningFrequency));
                break;
            case "personality":
                builder.and(stringEquals(criteriaMemberStat.getPersonality(), memberStat.personality));
                break;
            case "mbti":
                builder.and(stringEquals(criteriaMemberStat.getMbti(), memberStat.mbti));
                break;
            default:
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        }
    }

    private BooleanExpression stringEquals(String value, StringPath path) {
        return value == null ? null : path.eq(value);
    }

    private BooleanExpression integerEquals(Integer value, NumberPath<Integer> path) {
        return value == null ? null : path.eq(value);
    }

    private BooleanExpression booleanEquals(Boolean value, BooleanPath path) {
        return value == null ? null : path.eq(value);
    }

}
