package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.UUID;

public class S3KeyUtil {

    /**
     * 파일 확장자 추출
     */
    private static String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new GeneralException(ErrorStatus._FILE_EXTENSION_ERROR);
        }
    }

    /**
     * S3 Key 생성 (폴더 + timestamp + uuid + 확장자)
     * @param prefix 폴더명 (예: "posts", "profiles", "notices")
     * @param originalFileName 원본 파일명
     * @return S3에 업로드될 고유 key
     */
    public static String generateKey(String prefix, String originalFileName) {
        String ext = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        long ts = System.currentTimeMillis();
        return String.format("%s/%d_%s%s", prefix, ts, uuid, ext);
    }

}
