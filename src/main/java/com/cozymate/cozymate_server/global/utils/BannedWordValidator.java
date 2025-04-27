package com.cozymate.cozymate_server.global.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BannedWordValidator implements ConstraintValidator<BannedWordValid, String> {

    private static final Set<String> BANNED_WORDS = new HashSet<>();

    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = BannedWordValidator.class
                .getClassLoader()
                .getResourceAsStream("global/banned_words.json");

            if (inputStream != null) {
                JsonNode root = objectMapper.readTree(inputStream);
                JsonNode wordListNode = root.get("banned_word_list");

                if (wordListNode.isArray()) {
                    for (JsonNode node : wordListNode) {
                        BANNED_WORDS.add(node.asText());
                    }
                }
            } else {
                log.error("banned_words.json 파일을 찾을 수 없습니다.");
                throw new IllegalStateException("banned_words.json 파일을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            log.error("banned_words.json 파일을 읽는 도중 오류가 발생했습니다.");
            throw new RuntimeException("banned_words.json 파일을 읽는 도중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null은 다른 @NotBlank 같은 걸로 체크

        for (String bannedWord : BANNED_WORDS) {
            if (value.contains(bannedWord)) {
                return false;
            }
        }
        return true;
    }
}
