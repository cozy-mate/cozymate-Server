package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import static com.cozymate.cozymate_server.domain.member.QMember.member;
import static com.cozymate.cozymate_server.domain.memberstat.QMemberStat.memberStat;
import static com.cozymate.cozymate_server.domain.memberstatequality.QMemberStatEquality.memberStatEquality;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberStatQueryRepositoryImpl implements MemberStatQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final Integer NUM_OF_ROOMMATE_NOT_DETERMINED = 0;
    private static final String PERSONALITY = "personality";
    private static final String SLEEPING_HABIT = "sleepingHabit";
    private static final String NUM_OF_ROOMMATE = "numOfRoommate";

    private JPAQuery<Tuple> createBaseQuery(MemberStat criteriaMemberStat) {
        return queryFactory
            .select(memberStat, memberStatEquality.equality)
            .from(memberStat)
            .join(memberStat.member, member)
            .leftJoin(memberStatEquality)
            .on(memberStat.member.id.eq(memberStatEquality.memberBId)
                .and(memberStatEquality.memberAId.eq(criteriaMemberStat.getMember().getId())))
            .where(initDefaultQuery(criteriaMemberStat));
    }

    @Override
    public Map<Member, MemberStat> getFilteredMemberStat(List<String> filterList,
        MemberStat criteriaMemberStat) {
        // Tuple로 MemberStat과 Member를 함께 조회
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterList, criteriaMemberStat))
            .fetch();

        // 결과를 Map<Member, MemberStat>으로 변환
        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(member),         // key: Member
                tuple -> tuple.get(memberStat)      // value: MemberStat
            ));
    }

    @Override
    public Map<Member, MemberStat> getAdvancedFilteredMemberStat(HashMap<String, List<?>> filterMap,
        MemberStat criteriaMemberStat) {
        // Tuple로 MemberStat과 Member를 함께 조회
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .fetch();

        // 결과를 Map<Member, MemberStat>으로 변환
        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(member),         // key: Member
                tuple -> tuple.get(memberStat)      // value: MemberStat
            ));
    }


    private BooleanBuilder initDefaultQuery(MemberStat criteriaMemberStat) {

        Member criteriaMember = criteriaMemberStat.getMember();

        BooleanBuilder builder = new BooleanBuilder()
            .and(memberStat.id.ne(criteriaMemberStat.getId()))
            .and(member.gender.eq(criteriaMember.getGender()))
            .and(member.university.id.eq(criteriaMember.getUniversity().getId()))
            .and(memberStat.dormitoryType.eq(criteriaMemberStat.getDormitoryType()));

        // '미정'인 경우 인실 조건을 무시, 그렇지 않으면 인실 조건 추가
        if (!criteriaMemberStat.getNumOfRoommate().equals(NUM_OF_ROOMMATE_NOT_DETERMINED)) {
            builder.and(memberStat.numOfRoommate.eq(criteriaMemberStat.getNumOfRoommate()));
        }

        return builder;
    }

    // 단순 필터링 (criteriaMemberStat과 각 필드의 값이 일치하는지 확인)
    private BooleanBuilder applyFilters(List<String> filterList, MemberStat criteriaMemberStat) {
        BooleanBuilder builder = new BooleanBuilder();

        // filterList가 주어진 경우에만 필터링 수행
        if (filterList != null) {
            filterList.forEach(filter -> {
                // 기존의 getPathByKey를 사용하여 동적 필터링을 적용
                Path<?> path = getPathByKey(filter);
                Object criteriaValue = getFieldValueByKey(criteriaMemberStat, filter);

                if (path != null && criteriaValue != null) {
                    applyFilter(builder, filter, criteriaValue, criteriaMemberStat);
                }
            });
        }
        return builder;
    }

    public Page<Map<MemberStat, Integer>> getFilteredMemberStat(MemberStat criteriaMemberStat,
        List<String> filterList, Pageable pageable) {

        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterList, criteriaMemberStat))
            .orderBy(memberStatEquality.equality.desc()) // equality 기준으로 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 결과를 Map<MemberStat, Integer> 형식으로 변환
        List<Map<MemberStat, Integer>> resultList = results.stream()
            .map(tuple -> {
                MemberStat stat = tuple.get(memberStat);
                Integer equality = tuple.get(memberStatEquality.equality);
                Map<MemberStat, Integer> statMap = new HashMap<>();
                statMap.put(stat, equality != null ? equality : 0);
                return statMap;
            })
            .collect(Collectors.toList());

        return new PageImpl<>(resultList, pageable, results.size());
    }

    public Page<Map<MemberStat, Integer>> getAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable) {

        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .orderBy(memberStatEquality.equality.desc()) // equality 기준으로 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 결과를 Map<MemberStat, Integer> 형식으로 변환
        List<Map<MemberStat, Integer>> resultList = results.stream()
            .map(tuple -> {
                MemberStat stat = tuple.get(memberStat);
                Integer equality = tuple.get(memberStatEquality.equality);
                Map<MemberStat, Integer> statMap = new HashMap<>();
                statMap.put(stat, equality != null ? equality : 0);
                return statMap;
            })
            .collect(Collectors.toList());

        return new PageImpl<>(resultList, pageable, results.size());
    }


    // 상세 검색 필터링 (key : value) 필터링
    private BooleanBuilder applyFilters(HashMap<String, List<?>> filterMap,
        MemberStat criteriaMemberStat) {
        BooleanBuilder builder = new BooleanBuilder();
        if (filterMap != null) {
            filterMap.forEach((key, value) -> {
                // value가 빈 배열이 아닐때만 적용
                if (value != null && !value.isEmpty()) {
                    applyFilter(builder, key, value, criteriaMemberStat);
                }
            });
        }
        return builder;
    }

    private void applyFilter(BooleanBuilder builder, String filterKey, Object filterValue,
        MemberStat criteriaMemberStat) {

        if (Objects.isNull(filterValue)) {
            return;
        }

        Path<?> path = getPathByKey(filterKey);

        if (path == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        }

        switch (path.getClass().getSimpleName()) {
            case "StringPath" ->
                handleStringPathFilter(builder, (StringPath) path, filterKey, filterValue);
            case "NumberPath" ->
                handleNumberPathFilter(builder, (NumberPath<Integer>) path, filterKey, filterValue,
                    criteriaMemberStat);
            case "BooleanPath" -> builder.and(handleBooleanFilter((BooleanPath) path, filterValue));
            case "DatePath" ->
                builder.and(handleDateFilter((DatePath<LocalDate>) path, filterValue));
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        }
    }

    private void handleStringPathFilter(BooleanBuilder builder, StringPath stringPath,
        String filterKey, Object filterValue) {
        // 다중 선택 요소들의 경우
        if (filterKey.equals(PERSONALITY) || filterKey.equals(SLEEPING_HABIT)) {
            builder.and(handleMultiStringContentFilter(stringPath, filterValue));
        } else {
            builder.and(handleStringFilter(stringPath, filterValue));
        }
    }

    private void handleNumberPathFilter(BooleanBuilder builder, NumberPath<Integer> numberPath,
        String filterKey, Object filterValue, MemberStat criteriaMemberStat) {
        if (filterKey.equals(NUM_OF_ROOMMATE)) {
            builder.and(handleNumberOfRoommateFilter(numberPath, filterValue, criteriaMemberStat));
        } else {
            builder.and(handleNumberFilter(numberPath, filterValue));
        }
    }

    // 성격은 다중 선택으로 String의 조합으로 저장됨. 따라서 일반 String 필터링과 분리함.
    private BooleanExpression handleMultiStringContentFilter(StringPath path, Object filterValue) {
        if (filterValue instanceof String value) {
            return path.eq(value);
        } else if (filterValue instanceof List<?> values) {
            return values.stream()
                .map(String::valueOf)
                .map(value -> path.like("%" + value + "%"))
                .reduce(BooleanExpression::or)
                .orElse(null);
        }
        return null;
    }


    private BooleanExpression handleNumberOfRoommateFilter(NumberPath<Integer> path,
        Object filterValue, MemberStat criteriaMemberStat) {
        if (filterValue instanceof Integer value) {
            // 미정일 경우 모든 조건을 보여줘야 함
            if (value.equals(0)) {
                return null;
            }
            // 나머지는 자신의 인실에 맞춰서 보여줌
            return path.eq(value);
        } else if (filterValue instanceof List<?> values) {
            if (criteriaMemberStat.getNumOfRoommate().equals(NUM_OF_ROOMMATE_NOT_DETERMINED)) {
                // 미정을 선택한 사용자는 자유롭게 필터링 가능
                return path.in((List<Integer>) values);
            }
            // 특정 인실을 선택한 사용자는 필터링 불가능

            throw new GeneralException((ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE));

            // return null;
        }
        return null;
    }

    // 일반 String을 다루는 BooleanExpression
    private BooleanExpression handleStringFilter(StringPath path, Object filterValue) {
        if (filterValue instanceof String value) {
            return path.eq(value);
        } else if (filterValue instanceof List<?> values) {
            return path.in((List<String>) values);
        }
        return null;
    }

    // Integer 형식을 다루는 Boolean Expression
    private BooleanExpression handleNumberFilter(NumberPath<Integer> path, Object filterValue) {

        if (filterValue instanceof Integer value) {
            return path.eq(value);
        } else if (filterValue instanceof List<?> values) {
            return path.in((List<Integer>) values);
        }
        return null;
    }

    // Boolean 형식을 다루는 Boolean Expression
    private BooleanExpression handleBooleanFilter(BooleanPath path, Object filterValue) {
        if (filterValue instanceof Boolean value) {
            return path.eq(value);
        } else if (filterValue instanceof List<?> values) {
            return path.in((List<Boolean>) values);
        }
        return null;
    }

    // Date 형식(출생 년도)를 다루는 Boolean Expression
    private BooleanExpression handleDateFilter(DatePath<LocalDate> path, Object filterValue) {
        if (filterValue instanceof Integer value) {
            return Expressions.numberTemplate(Integer.class, "year({0})", path).eq(value);
        } else if (filterValue instanceof LocalDate value) {
            return Expressions.numberTemplate(Integer.class, "year({0})", path).eq(value.getYear());
        } else if (filterValue instanceof List<?> values) {
            List<Integer> value = values.stream()
                .map(Integer.class::cast)
                .toList();
            return Expressions.numberTemplate(Integer.class, "year({0})", path).in(value);
        }
        return null;
    }


    private Path<?> getPathByKey(String key) {
        return switch (key) {
            case "acceptance" -> memberStat.acceptance;
            case "admissionYear" -> memberStat.admissionYear;
            case "major" -> memberStat.major;
            case "wakeUpTime" -> memberStat.wakeUpTime;
            case "sleepingTime" -> memberStat.sleepingTime;
            case "turnOffTime" -> memberStat.turnOffTime;
            case "smoking" -> memberStat.smoking;
            case "sleepingHabit" -> memberStat.sleepingHabit;
            case "numOfRoommate" -> memberStat.numOfRoommate;
            case "airConditioningIntensity" -> memberStat.airConditioningIntensity;
            case "heatingIntensity" -> memberStat.heatingIntensity;
            case "lifePattern" -> memberStat.lifePattern;
            case "intimacy" -> memberStat.intimacy;
            case "canShare" -> memberStat.canShare;
            case "isPlayGame" -> memberStat.isPlayGame;
            case "isPhoneCall" -> memberStat.isPhoneCall;
            case "studying" -> memberStat.studying;
            case "intake" -> memberStat.intake;
            case "cleanSensitivity" -> memberStat.cleanSensitivity;
            case "noiseSensitivity" -> memberStat.noiseSensitivity;
            case "cleaningFrequency" -> memberStat.cleaningFrequency;
            case "personality" -> memberStat.personality;
            case "drinkingFrequency" -> memberStat.drinkingFrequency;
            case "mbti" -> memberStat.mbti;
            case "birthYear" -> member.birthDay;
            case "dormitoryType" -> memberStat.dormitoryType;
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        };
    }

    private Object getFieldValueByKey(MemberStat criteriaMemberStat, String key) {

        return switch (key) {
            case "acceptance" -> criteriaMemberStat.getAcceptance();
            case "admissionYear" -> criteriaMemberStat.getAdmissionYear();
            case "major" -> criteriaMemberStat.getMajor();
            case "wakeUpTime" -> criteriaMemberStat.getWakeUpTime();
            case "sleepingTime" -> criteriaMemberStat.getSleepingTime();
            case "turnOffTime" -> criteriaMemberStat.getTurnOffTime();
            case "smoking" -> criteriaMemberStat.getSmoking();
            case "sleepingHabit" -> criteriaMemberStat.getSleepingHabit();
            case "numOfRoommate" -> criteriaMemberStat.getNumOfRoommate();
            case "airConditioningIntensity" -> criteriaMemberStat.getAirConditioningIntensity();
            case "heatingIntensity" -> criteriaMemberStat.getHeatingIntensity();
            case "lifePattern" -> criteriaMemberStat.getLifePattern();
            case "intimacy" -> criteriaMemberStat.getIntimacy();
            case "canShare" -> criteriaMemberStat.getCanShare();
            case "isPlayGame" -> criteriaMemberStat.getIsPlayGame();
            case "isPhoneCall" -> criteriaMemberStat.getIsPhoneCall();
            case "studying" -> criteriaMemberStat.getStudying();
            case "intake" -> criteriaMemberStat.getIntake();
            case "cleanSensitivity" -> criteriaMemberStat.getCleanSensitivity();
            case "noiseSensitivity" -> criteriaMemberStat.getNoiseSensitivity();
            case "cleaningFrequency" -> criteriaMemberStat.getCleaningFrequency();
            case "personality" -> criteriaMemberStat.getPersonality();
            case "drinkingFrequency" -> criteriaMemberStat.getDrinkingFrequency();
            case "mbti" -> criteriaMemberStat.getMbti();
            case "birthYear" -> criteriaMemberStat.getMember().getBirthDay();
            case "dormitoryType" -> criteriaMemberStat.getDormitoryType();
            default -> null;
        };
    }
}
