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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberStatQueryRepositoryImpl implements MemberStatQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final Integer NUM_OF_ROOMMATE_NOT_DETERMINED = 0;
    private static final String PERSONALITY = "personality";
    private static final String SLEEPING_HABIT = "sleepingHabit";
    private static final String NUM_OF_ROOMMATE = "numOfRoommate";

    // 가장 기본이 되는 Base 쿼리 입니다.
    // MemberStat, Member를 조인하고,
    // 일치율이 없는 경우도 조인(LeftJoin)을 합니다.
    private JPAQuery<Tuple> createBaseQuery(MemberStat criteriaMemberStat) {
        return queryFactory
            .select(memberStat, memberStatEquality.equality)
            .from(memberStat)
            .join(memberStat.member, member)
            .leftJoin(memberStatEquality)
            .on(memberStatEquality.memberBId.eq(memberStat.member.id)
                .and(memberStatEquality.memberAId.eq(criteriaMemberStat.getMember().getId())))
            .where(initDefaultQuery(criteriaMemberStat));
    }

    // 가장 기본적으로 필터링 해야 하는 항목들을 넣어놓은 쿼리입니다.
    private BooleanBuilder initDefaultQuery(MemberStat criteriaMemberStat) {

        Member criteriaMember = criteriaMemberStat.getMember();

        BooleanBuilder builder = new BooleanBuilder()
            // 본인 제외하기
            .and(memberStat.id.ne(criteriaMemberStat.getId()))
            // 성별 필터링
            .and(member.gender.eq(criteriaMember.getGender()))
            .and(member.university.id.eq(criteriaMember.getUniversity().getId()));
//            .and(memberStat.dormitoryName.eq(criteriaMemberStat.getDormitoryName()));

//        // '미정'인 경우 인실 조건을 무시, 그렇지 않으면 인실 조건 추가
//        if (!criteriaMemberStat.getNumOfRoommate().equals(NUM_OF_ROOMMATE_NOT_DETERMINED)) {
//            builder.and(memberStat.numOfRoommate.eq(criteriaMemberStat.getNumOfRoommate()));
//        }

        return builder;
    }

    // 단순 필터링하는 필터 (기준 멤버 스탯과 각 필드의 값이 일치하는지 확인)
    private BooleanBuilder applyFilters(List<String> filterList, MemberStat criteriaMemberStat) {

        BooleanBuilder builder = new BooleanBuilder();

        // filterList가 주어진 경우에만 필터링 수행
        if (filterList != null) {
            filterList.forEach(filter -> {

                // 필터링 key가 잘못되었으면 null 반환
                Path<?> path = getPathByKey(filter);
                // key에 대한 value가 없어도 null 반환
                Object criteriaValue = getFieldValueByKey(criteriaMemberStat, filter);
                // FIXME: exception을 여기서 걸어줘야하는지 판단이 안 서네요
                if (path != null && criteriaValue != null) {
                    applyFilter(builder, filter, criteriaValue, criteriaMemberStat);
                }

            });
        }
        return builder;
    }

    // 단순 필터링시 서비스가 사용하는 메서드
    @Override
    public Slice<Map<MemberStat, Integer>> getFilteredMemberStat(MemberStat criteriaMemberStat,
        List<String> filterList, Pageable pageable) {

        // pageSize + 1만큼 데이터를 조회하여 다음 페이지 여부 확인
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterList, criteriaMemberStat))
            .orderBy(memberStatEquality.equality.desc(),
                member.nickname.asc(),
                memberStat.id.asc()) // equality 기준으로 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // 다음 페이지 확인을 위해 추가 데이터 조회
            .fetch();

        // results 크기를 기준으로 hasNext 계산
        boolean hasNext = results.size() > pageable.getPageSize();

        // 결과 리스트에서 초과된 요소를 제거하여 현재 페이지 데이터만 유지
        if (hasNext) {
            results.remove(results.size() - 1);
        }

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

        return new SliceImpl<>(resultList, pageable, hasNext);
    }

    // 상세 필터링시 서비스가 사용하는 메서드
    @Override
    public Slice<Map<MemberStat, Integer>> getAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable) {

        // pageSize + 1만큼 데이터를 조회하여 다음 페이지 여부 확인
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .orderBy(memberStatEquality.equality.desc(),
                member.nickname.asc(),
                memberStat.id.asc()) // equality 기준으로 정렬
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // 다음 페이지 확인을 위해 추가 데이터 조회
            .fetch();

        // results 크기를 기준으로 hasNext 계산
        boolean hasNext = results.size() > pageable.getPageSize();

        // 결과 리스트에서 초과된 요소를 제거하여 현재 페이지 데이터만 유지
        if (hasNext) {
            results.remove(results.size() - 1);
        }

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

        return new SliceImpl<>(resultList, pageable, hasNext);
    }

    // 상세 검색 필터링에 대한 멤버 개수 표시
    @Override
    public int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat, HashMap<String, List<?>> filterMap) {
        return createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .fetch()
            .size();
    }

    // 상세 검색 필터들을 iteration하며 필터를 적용하는 메서드 (key : value)
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


    // 상세 검색 필터
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
            case "StringPath" -> handleStringPathFilter(builder, (StringPath) path, filterKey, filterValue);
            case "NumberPath" -> handleNumberPathFilter(builder, (NumberPath<Integer>) path, filterKey, filterValue,
                    criteriaMemberStat);
            case "BooleanPath" -> builder.and(handleBooleanFilter((BooleanPath) path, filterValue));
            case "DatePath" -> builder.and(handleDateFilter((DatePath<LocalDate>) path, filterValue));
            default -> throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        }
    }

    // value가 String인 것들에 대한 필터
    private void handleStringPathFilter(BooleanBuilder builder, StringPath stringPath,
        String filterKey, Object filterValue) {
        // 다중 선택 요소들의 필터는 다시 분리(성격, 잠버릇)
        if (filterKey.equals(PERSONALITY) || filterKey.equals(SLEEPING_HABIT)) {
            builder.and(handleMultiStringContentFilter(stringPath, filterValue));
        } else {
            builder.and(handleStringFilter(stringPath, filterValue));
        }
    }

    // value가 숫자인 것들에 대한 필터
    // 인실은 예외 처리 때문에 함수를 따로 분리함
    private void handleNumberPathFilter(BooleanBuilder builder, NumberPath<Integer> numberPath,
        String filterKey, Object filterValue, MemberStat criteriaMemberStat) {
        if (filterKey.equals(NUM_OF_ROOMMATE)) {
            builder.and(handleNumberOfRoommateFilter(numberPath, filterValue, criteriaMemberStat));
        } else {
            builder.and(handleNumberFilter(numberPath, filterValue));
        }
    }


    // 다중 선택 요소들의 필터(성격, 잠버릇)
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

    // 인실에 대한 필터(예외 경우의 수 처리)
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


    // 멤버 스탯 String과 멤버 스탯의 값을 연결해주는 메서드입니다.
    private Path<?> getPathByKey(String key) {
        return switch (key) {
            case "acceptance" -> memberStat.acceptance;
            case "admissionYear" -> memberStat.admissionYear;
            case "majorName" -> memberStat.member.majorName;
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
            case "dormitoryName" -> memberStat.dormitoryName;
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        };
    }

    private Object getFieldValueByKey(MemberStat criteriaMemberStat, String key) {

        return switch (key) {
            case "acceptance" -> criteriaMemberStat.getAcceptance();
            case "admissionYear" -> criteriaMemberStat.getAdmissionYear();
            case "majorName" -> criteriaMemberStat.getMember().getMajorName();
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
            case "dormitoryName" -> criteriaMemberStat.getDormitoryName();
            default -> null;
        };
    }
}
