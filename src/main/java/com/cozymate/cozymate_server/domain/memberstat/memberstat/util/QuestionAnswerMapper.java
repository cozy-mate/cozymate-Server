package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionAnswerMapper {

    private static Map<String, List<String>> questionAnswerMap;
    private static final String JSON_FILE = "memberstat/question_answer.json";
    private static final String AM = "오전";
    private static final String PM = "오후";

    private static final List<String> MULTI_VALUE_QUESTION = List.of("personalities",
        "sleepingHabits");

    private static final List<String> NOT_LIFESTYLE = List.of("birthYear", "dormJoiningStatus",
        "admissionYear", "majorName", "dormName", "numOfRoommate");


    static {
        load();
    }

    private static void load() {
        if (questionAnswerMap == null) { // 중복 로딩 방지
            try (InputStream inputStream = QuestionAnswerMapper.class.getClassLoader()
                .getResourceAsStream(JSON_FILE)) {
                if (inputStream == null) {
                    log.error("파일을 찾을 수 없습니다: {}", JSON_FILE);
                    throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_NOT_FOUND);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                questionAnswerMap = objectMapper.readValue(inputStream,
                    new TypeReference<>() {
                    });
            } catch (IOException e) {
                log.error("JSON 파일 읽기 실패 {}: {}", JSON_FILE, e.getMessage());
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
            }
        }
    }

    public static int getIndex(String key, String value) {
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        int index = options.indexOf(value);
        if (index == -1) {
            log.error("key : {}. value : {}", key, value);
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return index;
    }

    public static String mapValue(String key, Integer index) {
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return Optional.ofNullable(options.get(index))
            .filter(value -> !value.isEmpty() && !value.isBlank())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR));

    }

    public static List<String> mapValues(String key, Integer bitmaskValue) {
        return getIndicesFromBitMask(bitmaskValue).stream()
            .map(index -> mapValue(key, index))
            .collect(Collectors.toList());
    }

    public static Integer convertTimeToInteger(String meridian, Integer hour) {
        if (meridian.equals(AM)) {
            return hour % 12;
        }
        return (hour % 12) + 12;
    }

    public static String calculateMeridian(Integer hour) {
        if (hour < 12) {
            return AM;
        }
        return PM;
    }

    public static Integer calculateHour(Integer hour) {
        int value = hour % 12;
        if (value == 0) {
            return 12;
        }
        return value;
    }

    public static List<Integer> getIndicesFromBitMask(int bitmask) {
        List<Integer> indices = new ArrayList<>();
        int index = 0;

        while (bitmask > 0) {
            // 비트가 1인지 확인
            if ((bitmask & 1) == 1) {
                indices.add(index);
            }
            bitmask >>= 1;
            index++;
        }

        return indices;
    }

    public static Integer convertBitMaskToInteger(String category, List<String> items) {
        int bitMask = 0;
        for (String item : items) {
            int index = getIndex(category, item);
            bitMask |= (1 << index);
        }
        return bitMask;
    }

    public static Map<String, String> convertToStringMap(Map<String, Object> rawMap) {
        return rawMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Integer && questionAnswerMap.containsKey(key)) {
                        if (MULTI_VALUE_QUESTION.stream().anyMatch(q -> q.equals(key))) {
                            List<String> mappedValues = mapValues(key, (Integer) value);
                            return String.join(", ", mappedValues); // 여러 값을 콤마로 구분
                        }

                        return mapValue(key, (Integer) value);
                    }

                    return value.toString(); // Integer가 아니면 그냥 String 변환
                },
                (o1, o2) -> o1,
                LinkedHashMap::new // fieldNameList 조회된 순서 유지
            ));
    }

    public static Map<String, List<?>> convertFilterMap(Map<String, List<?>> filterMap) {
        return filterMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String key = entry.getKey();
                    List<?> values = entry.getValue();

                    if (NOT_LIFESTYLE.contains(key)) {
                        return values;
                    }

                    if (MULTI_VALUE_QUESTION.contains(key)) {
                        return List.of(convertBitMaskToInteger(
                            key, values.stream().map(Object::toString).toList()
                        ));
                    }

                    return values.stream()
                        .map(value -> getIndex(key, value.toString()))
                        .toList();
                }
            ));
    }

    public static List<String> getOptions(String key) {
        List<String> options = questionAnswerMap.get(key);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return options;
    }

}

