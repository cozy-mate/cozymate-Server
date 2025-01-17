package com.cozymate.cozymate_server.domain.memberstat_v2.util;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionAnswerMapper {

    private static Map<String, List<String>> questionAnswerMap;

    public static void load(String jsonFilePath) {
        if (questionAnswerMap == null) { // 중복 로딩 방지
            try (InputStream inputStream = QuestionAnswerMapper.class.getClassLoader()
                .getResourceAsStream(jsonFilePath)) {
                if (inputStream == null) {
                    log.error("파일을 찾을 수 없습니다: {}", jsonFilePath);
                    throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_NOT_FOUND);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                questionAnswerMap = objectMapper.readValue(inputStream,
                    new TypeReference<Map<String, List<String>>>() {
                    });
            } catch (IOException e) {
                log.error("JSON 파일 읽기 실패 {}: {}", jsonFilePath, e.getMessage());
                throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
            }
        }
    }

    public static int mapValue(String key, String value) {
        List<String> options = questionAnswerMap.get(key);
        log.debug("key:{}, value:{}", key, value);
        if (options == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        int index = options.indexOf(value);
        log.debug("index:{}", index);
        if (index == -1) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR);
        }
        return index;
    }
}

