package com.cozymate.cozymate_server.domain.memberstat_v2.repository.querydsl;

import static com.cozymate.cozymate_server.domain.member.QMember.member;
import static com.cozymate.cozymate_server.domain.memberstat_v2.QLifestyleMatchRate.lifestyleMatchRate;
import static com.cozymate.cozymate_server.domain.memberstat_v2.QMemberStatTest.memberStatTest;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
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
public class MemberStatQueryRepositoryImpl_v2 implements MemberStatQueryRepository_v2 {

    private final JPAQueryFactory queryFactory;

    private static final String NUM_OF_ROOMMATE_NOT_DETERMINED = "0";
    private static final String[] MULTI_ANSWERS = {"personality", "sleepingHabit"};
    private static final String NUM_OF_ROOMMATE = "numOfRoommate";

    @Override
    public Slice<Map<MemberStatTest, Integer>> getFilteredMemberStat(
        MemberStatTest criteriaMemberStat,
        List<String> filterList, Pageable pageable) {

        // pageSize + 1만큼 데이터를 조회하여 다음 페이지 여부 확인
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterList, criteriaMemberStat))
            .orderBy(lifestyleMatchRate.matchRate.desc(),
                member.nickname.asc(),
                memberStatTest.id.asc()) // equality 기준으로 정렬
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
        List<Map<MemberStatTest, Integer>> resultList = results.stream()
            .map(tuple -> {
                MemberStatTest stat = tuple.get(memberStatTest);
                Integer equality = tuple.get(lifestyleMatchRate.matchRate);
                Map<MemberStatTest, Integer> statMap = new HashMap<>();
                statMap.put(stat, equality != null ? equality : 0);
                return statMap;
            })
            .collect(Collectors.toList());

        return new SliceImpl<>(resultList, pageable, hasNext);
    }

    @Override
    public Slice<Map<MemberStatTest, Integer>> getAdvancedFilteredMemberStat(
        MemberStatTest criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable) {

        // pageSize + 1만큼 데이터를 조회하여 다음 페이지 여부 확인
        List<Tuple> results = createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .orderBy(lifestyleMatchRate.matchRate.desc(),
                member.nickname.asc(),
                memberStatTest.id.asc()) // equality 기준으로 정렬
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
        List<Map<MemberStatTest, Integer>> resultList = results.stream()
            .map(tuple -> {
                MemberStatTest stat = tuple.get(memberStatTest);
                Integer equality = tuple.get(lifestyleMatchRate.matchRate);
                Map<MemberStatTest, Integer> statMap = new HashMap<>();
                statMap.put(stat, equality != null ? equality : 0);
                return statMap;
            })
            .collect(Collectors.toList());

        return new SliceImpl<>(resultList, pageable, hasNext);
    }

    // 상세 검색 필터링에 대한 멤버 개수 표시
    @Override
    public int countAdvancedFilteredMemberStat(MemberStatTest criteriaMemberStat,
        HashMap<String, List<?>> filterMap) {
        return createBaseQuery(criteriaMemberStat)
            .where(applyFilters(filterMap, criteriaMemberStat))
            .fetch()
            .size();
    }

    private JPAQuery<Tuple> createBaseQuery(MemberStatTest criteriaMemberStat) {
        return queryFactory
            .select(memberStatTest, lifestyleMatchRate.matchRate)
            .from(memberStatTest)
            .join(memberStatTest.member, member)
            .leftJoin(lifestyleMatchRate)
            .on(
                (lifestyleMatchRate.id.memberA.eq(memberStatTest.member.id)
                    .and(lifestyleMatchRate.id.memberB.eq(criteriaMemberStat.getMember().getId())))
                    .or(
                        lifestyleMatchRate.id.memberB.eq(memberStatTest.member.id)
                            .and(lifestyleMatchRate.id.memberA.eq(
                                criteriaMemberStat.getMember().getId()))
                    )
            )
            .where(initDefaultQuery(criteriaMemberStat));
    }

    // 가장 기본적으로 필터링 해야 하는 항목들을 넣어놓은 쿼리입니다.
    private BooleanBuilder initDefaultQuery(MemberStatTest criteriaMemberStat) {

        Member criteriaMember = criteriaMemberStat.getMember();

        return new BooleanBuilder()
            // 본인 제외하기
            .and(memberStatTest.id.ne(criteriaMemberStat.getId()))
            // 성별 필터링
            .and(member.gender.eq(criteriaMember.getGender()))
            // 대학 일치 여부
            .and(member.university.id.eq(criteriaMember.getUniversity().getId()));
    }

    // 단순 필터링하는 필터 (기준 멤버 스탯과 각 필드의 값이 일치하는지 확인)
    private BooleanBuilder applyFilters(List<String> filterList,
        MemberStatTest criteriaMemberStat) {

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

    // 단순 필터링하는 필터 (기준 멤버 스탯과 각 필드의 값이 일치하는지 확인)
    // 상세 검색 필터들을 iteration하며 필터를 적용하는 메서드 (key : value)
    private BooleanBuilder applyFilters(HashMap<String, List<?>> filterMap,
        MemberStatTest criteriaMemberStat) {
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
        MemberStatTest criteriaMemberStat) {

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
        String filterKey, Object filterValue, MemberStatTest criteriaMemberStat) {
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
    private BooleanExpression handleMultiAnswersContentFilter(NumberPath<Integer> path,
        Object filterValue) {
        if (filterValue instanceof Integer value) {
            // filterValue가 1일 경우, 1번 비트만 켜진 값들을 반환
            return Expressions.booleanTemplate("{0} & {1} = {1}", path, value);
        } else if (filterValue instanceof List<?> values) {
            // filterValue가 [1, 4]와 같은 리스트일 경우
            Integer bitmask = 0;
            for (Object val : values) {
                if (val instanceof Integer) {
                    bitmask |= (Integer) val; // 각 비트 값을 OR 연산하여 하나의 비트마스크 생성
                }
            }
            // 생성된 비트마스크와 path의 비트 AND 연산을 하여, 해당 비트들이 모두 켜진 값들을 반환
            return Expressions.booleanTemplate("{0} & {1} = {1}", path, bitmask);
        }
        return null;
    }


    // 인실에 대한 필터(예외 경우의 수 처리)
    private BooleanExpression handleNumberOfRoommateFilter(StringPath path,
        Object filterValue, MemberStatTest criteriaMemberStat) {

        if (filterValue instanceof String value) {
            // "0"이면 모든 조건을 보여줘야 함
            if (value.equals("0")) {
                return null;  // 필터링 없이 모든 결과를 보여줌
            }
            // "0"이 아닌 값이면 해당 값과 일치하는 값만 보여줌
            return path.eq(value);  // numOfRoommate 값이 일치하는 것만 필터링
        } else if (filterValue instanceof List<?> values) {
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
            case "majorName" -> memberStatTest.member.majorName;
            case "birthYear" -> member.birthDay;

            case "acceptance" -> memberStatTest.memberUniversityStat.acceptance;
            case "admissionYear" -> memberStatTest.memberUniversityStat.admissionYear;
            case "numOfRoommate" -> memberStatTest.memberUniversityStat.numberOfRoommate;
            case "dormitoryName" -> memberStatTest.memberUniversityStat.dormitoryName;

            case "wakeUpTime" -> memberStatTest.lifestyle.wakeUpTime;
            case "sleepingTime" -> memberStatTest.lifestyle.sleepingTime;
            case "turnOffTime" -> memberStatTest.lifestyle.turnOffTime;
            case "smoking" -> memberStatTest.lifestyle.smokingStatus;
            case "sleepingHabit" -> memberStatTest.lifestyle.sleepingHabit;
            case "airConditioningIntensity" -> memberStatTest.lifestyle.coolingIntensity;
            case "heatingIntensity" -> memberStatTest.lifestyle.heatingIntensity;
            case "lifePattern" -> memberStatTest.lifestyle.lifePattern;
            case "intimacy" -> memberStatTest.lifestyle.intimacy;
            case "canShare" -> memberStatTest.lifestyle.itemSharing;
            case "isPlayGame" -> memberStatTest.lifestyle.playingGameFrequency;
            case "isPhoneCall" -> memberStatTest.lifestyle.phoneCallingFrequency;
            case "studying" -> memberStatTest.lifestyle.studyingFrequency;
            case "intake" -> memberStatTest.lifestyle.eatingFrequency;
            case "cleanSensitivity" -> memberStatTest.lifestyle.cleannessSensitivity;
            case "noiseSensitivity" -> memberStatTest.lifestyle.noiseSensitivity;
            case "cleaningFrequency" -> memberStatTest.lifestyle.cleaningFrequency;
            case "personality" -> memberStatTest.lifestyle.personality;
            case "drinkingFrequency" -> memberStatTest.lifestyle.drinkingFrequency;
            case "mbti" -> memberStatTest.lifestyle.mbti;
            default ->
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID);
        };
    }

    private Object getFieldValueByKey(MemberStatTest criteriaMemberStat, String key) {

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
