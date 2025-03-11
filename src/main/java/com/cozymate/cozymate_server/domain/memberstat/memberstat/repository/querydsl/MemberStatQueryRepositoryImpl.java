package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.querydsl;

import static com.cozymate.cozymate_server.domain.member.QMember.member;
import static com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.QLifestyleMatchRate.lifestyleMatchRate;
import static com.cozymate.cozymate_server.domain.memberstat.memberstat.QMemberStat.memberStat;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberStatQueryRepositoryImpl implements MemberStatQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final String NUM_OF_ROOMMATE_NOT_DETERMINED = "0";
    private static final String[] MULTI_ANSWERS = {"personality", "sleepingHabit"};
    private static final String NUM_OF_ROOMMATE = "numOfRoommate";

    @Override
    public Slice<Map<MemberStat, Integer>> filterByLifestyleAttributeList(
        MemberStat criteriaMemberStat,
        List<String> filterList, Pageable pageable) {

        return filterMemberStat(criteriaMemberStat,
            applyFilters(filterList, criteriaMemberStat), pageable);
    }

    @Override
    public Slice<Map<MemberStat, Integer>> filterByLifestyleValueMap(
        MemberStat criteriaMemberStat,
        Map<String, List<?>> filterMap, Pageable pageable) {

        return filterMemberStat(criteriaMemberStat,
            applyFilters(filterMap, criteriaMemberStat), pageable);
    }

    // 상세 검색 필터링에 대한 멤버 개수 표시
    @Override
    public int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        Map<String, List<?>> filterMap) {
        return createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .fetch()
            .size();
    }

    @Override
    public Map<MemberStat, Integer> getMemberStatsWithKeywordAndMatchRate(
        MemberStat criteriaMemberStat,
        String substring) {

        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(memberStat.member.nickname.like("%" + substring + "%")) // 닉네임 조건 추가
            .orderBy(lifestyleMatchRate.matchRate.desc(),
                member.nickname.asc(),
                memberStat.id.asc())
            .fetch();

        return results.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(memberStat),
                tuple -> {
                    Integer matchRate = tuple.get(lifestyleMatchRate.matchRate);
                    return (matchRate != null) ? matchRate : 0; // matchRate가 null이면 0으로 처리
                },
                (oldValue, newValue) -> oldValue, // 중복 키 발생 시 기존 값 유지
                LinkedHashMap::new // 순서 보장
            ));
    }

    private Slice<Map<MemberStat, Integer>> filterMemberStat(
        MemberStat criteriaMemberStat, BooleanBuilder filters, Pageable pageable) {

        // pageSize + 1만큼 데이터를 조회하여 다음 페이지 여부 확인
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(filters)
            .orderBy(lifestyleMatchRate.matchRate.desc(), member.nickname.asc(),
                memberStat.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1) // 다음 페이지 확인을 위해 추가 데이터 조회
            .fetch();

        // hasNext 계산 및 초과 데이터 제거
        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        // 결과를 Map<MemberStat, Integer> 형식으로 변환
        List<Map<MemberStat, Integer>> resultList = results.stream()
            .map(tuple -> {
                MemberStat stat = tuple.get(memberStat);
                Integer matchRate = tuple.get(lifestyleMatchRate.matchRate);
                Map<MemberStat, Integer> statMap = new HashMap<>();
                statMap.put(stat, matchRate != null ? matchRate : 0);
                return statMap;
            })
            .collect(Collectors.toList());

        return new SliceImpl<>(resultList, pageable, hasNext);

    }

    private JPAQuery<Tuple> createBaseQuery(MemberStat criteriaMemberStat) {
        return queryFactory
            .select(memberStat, lifestyleMatchRate.matchRate)
            .from(memberStat)
            .join(memberStat.member, member)
            .leftJoin(lifestyleMatchRate)
            .on(
                (lifestyleMatchRate.id.memberA.eq(memberStat.member.id).and(
                    lifestyleMatchRate.id.memberB.eq(criteriaMemberStat.getMember().getId()))).or(
                    lifestyleMatchRate.id.memberB.eq(memberStat.member.id)
                        .and(lifestyleMatchRate.id.memberA.eq(
                            criteriaMemberStat.getMember().getId()))
                )
            )
            .where(initDefaultQuery(criteriaMemberStat));
    }

    // 가장 기본적으로 필터링 해야 하는 항목들을 넣어놓은 쿼리입니다.
    private BooleanBuilder initDefaultQuery(MemberStat criteriaMemberStat) {

        Member criteriaMember = criteriaMemberStat.getMember();

        return new BooleanBuilder()
            // 본인 제외하기
            .and(memberStat.id.ne(criteriaMemberStat.getId()))
            // 성별 필터링
            .and(member.gender.eq(criteriaMember.getGender()))
            // 대학 일치 여부
            .and(member.university.id.eq(criteriaMember.getUniversity().getId()));
    }

    // 단순 필터링하는 필터 (기준 멤버 스탯과 각 필드의 값이 일치하는지 확인)
    private BooleanBuilder applyFilters(List<String> filterList,
        MemberStat criteriaMemberStat) {

        BooleanBuilder builder = new BooleanBuilder();

        // filterList가 주어진 경우에만 필터링 수행
        if (filterList == null) {
            return builder;
        }

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

        return builder;
    }

    // 단순 필터링하는 필터 (기준 멤버 스탯과 각 필드의 값이 일치하는지 확인)
    // 상세 검색 필터들을 iteration하며 필터를 적용하는 메서드 (key : value)
    private BooleanBuilder applyFilters(Map<String, List<?>> filterMap,
        MemberStat criteriaMemberStat) {
        BooleanBuilder builder = new BooleanBuilder();
        if (filterMap == null) {
            return builder;
        }

        filterMap.forEach((key, value) -> {
            // value가 빈 배열이 아닐때만 적용
            if (value != null && !value.isEmpty()) {
                applyFilter(builder, key, value, criteriaMemberStat);
            }
        });

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
                handleStringPathFilter(builder, (StringPath) path, filterKey, filterValue,
                    criteriaMemberStat);
            case "NumberPath" ->
                handleNumberPathFilter(builder, (NumberPath<Integer>) path, filterKey, filterValue);
            case "BooleanPath" -> builder.and(handleBooleanFilter((BooleanPath) path, filterValue));
            case "DatePath" ->
                builder.and(handleDateFilter((DatePath<LocalDate>) path, filterValue));
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        }
    }

    // 인실은 예외 처리 때문에 함수를 따로 분리함
    private void handleStringPathFilter(BooleanBuilder builder, StringPath stringPath,
        String filterKey, Object filterValue, MemberStat criteriaMemberStat) {
        // 인실은 분리
        if (filterKey.equals(NUM_OF_ROOMMATE)) {
            builder.and(handleNumberOfRoommateFilter(stringPath, filterValue, criteriaMemberStat));
        } else {
            builder.and(handleStringFilter(stringPath, filterValue));
        }
    }

    // value가 숫자인 것들에 대한 필터
    private void handleNumberPathFilter(BooleanBuilder builder, NumberPath<Integer> numberPath,
        String filterKey, Object filterValue) {
        if (Arrays.stream(MULTI_ANSWERS).toList().contains(filterKey)) {
            builder.and(handleMultiAnswersContentFilter(numberPath, filterValue));
        } else {
            builder.and(handleNumberFilter(numberPath, filterValue));
        }
    }


    // 다중 선택 요소들의 필터(성격, 잠버릇)
    private BooleanExpression handleMultiAnswersContentFilter(NumberPath<Integer> path, Object filterValue) {
        if (filterValue instanceof Integer value) {
            List<Integer> matchingValues = getMatchingValues(value);
            return matchingValues.isEmpty() ? Expressions.TRUE : path.in(matchingValues);
        }

        if (filterValue instanceof List<?> values) {
            Set<Integer> matchingValuesSet = values.stream()
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .flatMap(bitValue -> getMatchingValues(bitValue).stream())
                .collect(Collectors.toSet());

            return matchingValuesSet.isEmpty() ? Expressions.TRUE : path.in(matchingValuesSet);
        }

        return Expressions.TRUE;
    }

    /**
     * 특정 비트가 켜져 있는 값들을 미리 계산하여 반환
     */
    private List<Integer> getMatchingValues(int bitmask) {
        return IntStream.rangeClosed(0, 4095)
            .filter(i -> (i & bitmask) != 0)
            .boxed()
            .collect(Collectors.toList());
    }




    // 인실에 대한 필터(예외 경우의 수 처리)
    private BooleanExpression handleNumberOfRoommateFilter(StringPath path,
        Object filterValue, MemberStat criteriaMemberStat) {

        if (filterValue instanceof String value) {
            // "0"이면 모든 조건을 보여줘야 함
            if (value.equals("0")) {
                return null;  // 필터링 없이 모든 결과를 보여줌
            }
            // "0"이 아닌 값이면 해당 값과 일치하는 값만 보여줌
            return path.eq(value);  // numOfRoommate 값이 일치하는 것만 필터링
        }
        if (filterValue instanceof List<?> values) {
            // 리스트일 경우, 사용자가 '미정'을 선택한 경우에만 다중 선택 가능
            if (criteriaMemberStat.getMemberUniversityStat().getNumberOfRoommate()
                .equals(NUM_OF_ROOMMATE_NOT_DETERMINED)) {
                return path.in((List<String>) values);  // 여러 값에 대해 필터링 가능
            }
            // 특정 인실을 선택한 경우에는 필터링을 허용하지 않음
            throw new GeneralException((ErrorStatus._MEMBERSTAT_FILTER_CANNOT_FILTER_ROOMMATE));
        }
        return null;
    }


    // 일반 String을 다루는 BooleanExpression
    private BooleanExpression handleStringFilter(StringPath path, Object filterValue) {
        if (filterValue instanceof String value) {
            return path.eq(value);
        }
        if (filterValue instanceof List<?> values) {
            return path.in((List<String>) values);
        }
        return null;
    }

    // Integer 형식을 다루는 Boolean Expression
    private BooleanExpression handleNumberFilter(NumberPath<Integer> path, Object filterValue) {

        if (filterValue instanceof Integer value) {
            return path.eq(value);
        }
        if (filterValue instanceof List<?> values) {
            return path.in((List<Integer>) values);
        }
        return null;
    }

    // Boolean 형식을 다루는 Boolean Expression
    private BooleanExpression handleBooleanFilter(BooleanPath path, Object filterValue) {
        if (filterValue instanceof Boolean value) {
            return path.eq(value);
        }
        if (filterValue instanceof List<?> values) {
            return path.in((List<Boolean>) values);
        }
        return null;
    }

    // Date 형식(출생 년도)를 다루는 Boolean Expression
    private BooleanExpression handleDateFilter(DatePath<LocalDate> path, Object filterValue) {
        if (filterValue instanceof Integer value) {
            return Expressions.numberTemplate(Integer.class, "year({0})", path).eq(value);
        }
        if (filterValue instanceof LocalDate value) {
            return Expressions.numberTemplate(Integer.class, "year({0})", path).eq(value.getYear());
        }
        if (filterValue instanceof List<?> values) {
            List<Integer> value = values.stream()
                .map(Integer.class::cast)
                .toList();
            return Expressions.numberTemplate(Integer.class, "year({0})", path).in(value);
        }
        return null;
    }

    private Path<?> getPathByKey(String key) {
        return switch (key) {
            case "majorName" -> memberStat.member.majorName;
            case "birthYear" -> memberStat.member.birthDay;

            case "acceptance" -> memberStat.memberUniversityStat.acceptance;
            case "admissionYear" -> memberStat.memberUniversityStat.admissionYear;
            case "numOfRoommate" -> memberStat.memberUniversityStat.numberOfRoommate;
            case "dormitoryName" -> memberStat.memberUniversityStat.dormitoryName;

            case "wakeUpTime" -> memberStat.lifestyle.wakeUpTime;
            case "sleepingTime" -> memberStat.lifestyle.sleepingTime;
            case "turnOffTime" -> memberStat.lifestyle.turnOffTime;
            case "smoking" -> memberStat.lifestyle.smokingStatus;
            case "sleepingHabit" -> memberStat.lifestyle.sleepingHabit;
            case "airConditioningIntensity" -> memberStat.lifestyle.coolingIntensity;
            case "heatingIntensity" -> memberStat.lifestyle.heatingIntensity;
            case "lifePattern" -> memberStat.lifestyle.lifePattern;
            case "intimacy" -> memberStat.lifestyle.intimacy;
            case "canShare" -> memberStat.lifestyle.itemSharing;
            case "isPlayGame" -> memberStat.lifestyle.playingGameFrequency;
            case "isPhoneCall" -> memberStat.lifestyle.phoneCallingFrequency;
            case "studying" -> memberStat.lifestyle.studyingFrequency;
            case "intake" -> memberStat.lifestyle.eatingFrequency;
            case "cleanSensitivity" -> memberStat.lifestyle.cleannessSensitivity;
            case "noiseSensitivity" -> memberStat.lifestyle.noiseSensitivity;
            case "cleaningFrequency" -> memberStat.lifestyle.cleaningFrequency;
            case "personality" -> memberStat.lifestyle.personality;
            case "drinkingFrequency" -> memberStat.lifestyle.drinkingFrequency;
            case "mbti" -> memberStat.lifestyle.mbti;
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        };
    }

    private Object getFieldValueByKey(MemberStat criteriaMemberStat, String key) {

        return switch (key) {
            case "majorName" -> criteriaMemberStat.getMember().getMajorName();
            case "birthYear" -> criteriaMemberStat.getMember().getBirthDay();

            case "acceptance" -> criteriaMemberStat.getMemberUniversityStat().getAcceptance();
            case "admissionYear" -> criteriaMemberStat.getMemberUniversityStat().getAdmissionYear();
            case "numOfRoommate" ->
                criteriaMemberStat.getMemberUniversityStat().getNumberOfRoommate();
            case "dormitoryName" -> criteriaMemberStat.getMemberUniversityStat().getDormitoryName();

            case "wakeUpTime" -> criteriaMemberStat.getLifestyle().getWakeUpTime();
            case "sleepingTime" -> criteriaMemberStat.getLifestyle().getSleepingTime();
            case "turnOffTime" -> criteriaMemberStat.getLifestyle().getTurnOffTime();
            case "smoking" -> criteriaMemberStat.getLifestyle().getSmokingStatus();
            case "sleepingHabit" -> criteriaMemberStat.getLifestyle().getSleepingHabit();
            case "airConditioningIntensity" ->
                criteriaMemberStat.getLifestyle().getCoolingIntensity();
            case "heatingIntensity" -> criteriaMemberStat.getLifestyle().getHeatingIntensity();
            case "lifePattern" -> criteriaMemberStat.getLifestyle().getLifePattern();
            case "intimacy" -> criteriaMemberStat.getLifestyle().getIntimacy();
            case "canShare" -> criteriaMemberStat.getLifestyle().getItemSharing();
            case "isPlayGame" -> criteriaMemberStat.getLifestyle().getPlayingGameFrequency();
            case "isPhoneCall" -> criteriaMemberStat.getLifestyle().getPhoneCallingFrequency();
            case "studying" -> criteriaMemberStat.getLifestyle().getStudyingFrequency();
            case "intake" -> criteriaMemberStat.getLifestyle().getEatingFrequency();
            case "cleanSensitivity" -> criteriaMemberStat.getLifestyle().getCleannessSensitivity();
            case "noiseSensitivity" -> criteriaMemberStat.getLifestyle().getNoiseSensitivity();
            case "cleaningFrequency" -> criteriaMemberStat.getLifestyle().getCleaningFrequency();
            case "personality" -> criteriaMemberStat.getLifestyle().getPersonality();
            case "drinkingFrequency" -> criteriaMemberStat.getLifestyle().getDrinkingFrequency();
            case "mbti" -> criteriaMemberStat.getLifestyle().getMbti();
            default -> null;
        };
    }


}
